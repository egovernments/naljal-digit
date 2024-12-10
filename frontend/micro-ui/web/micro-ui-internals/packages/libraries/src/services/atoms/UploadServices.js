import Axios from "axios";
import Urls from "./urls";

const getDynamicPart = (url) => {
  const parsedUrl = new URL(url);
  const pathParts = parsedUrl.pathname.split('/').filter(Boolean);
  return pathParts.length > 0 ? pathParts[0] : null; // Gets the first part after the domain
};

export const UploadServices = {
  Filestorage: async (module, filedata, tenantId) => {
    const formData = new FormData();

    formData.append("file", filedata, filedata.name);
    formData.append("tenantId", tenantId);
    formData.append("module", module);
    let tenantInfo=window?.globalConfigs?.getConfig("ENABLE_SINGLEINSTANCE")?`?tenantId=${tenantId}`:"";
    var config = {
      method: "post",
      url:`${Urls.FileStore}${tenantInfo}`,   
      data: formData,
      headers: { "auth-token": Digit.UserService.getUser() ? Digit.UserService.getUser()?.access_token : null},
    };

    Axios.defaults.baseURL = `https://naljalseva.jjm.gov.in/${getDynamicPart(window?.location?.href)}/`;
    return Axios(config);
  },

  // ${getDynamicPart(window?.location?.href)}

  MultipleFilesStorage: async (module, filesData, tenantId) => {
    const formData = new FormData();
    const filesArray = Array.from(filesData)
    filesArray?.forEach((fileData, index) => fileData ? formData.append("file", fileData, fileData.name) : null);
    formData.append("tenantId", tenantId);
    formData.append("module", module);
    let tenantInfo=window?.globalConfigs?.getConfig("ENABLE_SINGLEINSTANCE")?`?tenantId=${tenantId}`:"";
    var config = {
      method: "post",
      url:`${Urls.FileStore}${tenantInfo}`, 
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data',"auth-token": Digit.UserService.getUser().access_token },
    };
    Axios.defaults.baseURL = `https://naljalseva.jjm.gov.in/${getDynamicPart(window?.location?.href)}/`;
    return Axios(config);
  },

  Filefetch: async (filesArray, tenantId) => {
    let tenantInfo=window?.globalConfigs?.getConfig("ENABLE_SINGLEINSTANCE")?`?tenantId=${tenantId}`:"";
    var config = {
      method: "get",
      url:`${Urls.FileFetch}${tenantInfo}`, 
      params: {
        tenantId: tenantId,
        fileStoreIds: filesArray?.join(","),
      },
    };
    const res = await Axios(config);
    return res;
  },
};
