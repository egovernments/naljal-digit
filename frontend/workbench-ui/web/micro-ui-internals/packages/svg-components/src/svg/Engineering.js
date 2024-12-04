import React from "react";
import PropTypes from "prop-types";

export const Engineering = ({ className, height = "24", width = "24", style = {}, fill = "#F47738", onClick = null }) => {
  return (
    <svg width={width} height={height} className={className} onClick={onClick} style={style} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <g clip-path="url(#clip0_176_690)">
        <path d="M9 15C6.33 15 1 16.34 1 19V21H17V19C17 16.34 11.67 15 9 15Z" fill={fill} />
        <path
          d="M22.0998 6.84C22.1098 6.73 22.1198 6.62 22.1198 6.5C22.1198 6.38 22.1098 6.27 22.0898 6.16L22.8298 5.58C22.8998 5.53 22.9098 5.43 22.8698 5.36L22.1698 4.15C22.1298 4.07 22.0298 4.05 21.9598 4.07L21.0998 4.42C20.9198 4.28 20.7198 4.17 20.5098 4.08L20.3798 3.15C20.3598 3.06 20.2898 3 20.1998 3H18.7998C18.7098 3 18.6398 3.06 18.6298 3.15L18.4998 4.08C18.2898 4.17 18.0898 4.29 17.9098 4.42L17.0398 4.07C16.9598 4.04 16.8698 4.07 16.8298 4.15L16.1298 5.36C16.0898 5.44 16.0998 5.53 16.1698 5.58L16.9098 6.16C16.8898 6.27 16.8798 6.39 16.8798 6.5C16.8798 6.61 16.8898 6.73 16.9098 6.84L16.1698 7.42C16.0998 7.47 16.0898 7.57 16.1298 7.64L16.8298 8.85C16.8698 8.93 16.9698 8.95 17.0398 8.93L17.9098 8.58C18.0898 8.72 18.2898 8.83 18.4998 8.92L18.6298 9.85C18.6398 9.94 18.7098 10 18.7998 10H20.1998C20.2898 10 20.3598 9.94 20.3698 9.85L20.4998 8.92C20.7098 8.83 20.9098 8.71 21.0898 8.58L21.9598 8.93C22.0398 8.96 22.1298 8.93 22.1698 8.85L22.8698 7.64C22.9098 7.56 22.8998 7.47 22.8298 7.42L22.0998 6.84ZM19.4998 7.75C18.8098 7.75 18.2498 7.19 18.2498 6.5C18.2498 5.81 18.8098 5.25 19.4998 5.25C20.1898 5.25 20.7498 5.81 20.7498 6.5C20.7498 7.19 20.1898 7.75 19.4998 7.75Z"
          fill={fill}
        />
        <path
          d="M19.9199 11.68L19.4199 10.81C19.3899 10.75 19.3199 10.73 19.2699 10.75L18.6499 11C18.5199 10.9 18.3799 10.82 18.2299 10.76L18.1399 10.1C18.1199 10.04 18.0599 10 17.9999 10H16.9999C16.9399 10 16.8899 10.04 16.8799 10.11L16.7899 10.77C16.6399 10.83 16.4999 10.92 16.3699 11.01L15.7499 10.76C15.6899 10.74 15.6299 10.76 15.5999 10.82L15.0999 11.69C15.0699 11.75 15.0799 11.81 15.1299 11.85L15.6599 12.26C15.6499 12.34 15.6399 12.42 15.6399 12.5C15.6399 12.58 15.6499 12.67 15.6599 12.74L15.1299 13.15C15.0799 13.19 15.0699 13.26 15.0999 13.31L15.5999 14.18C15.6299 14.24 15.6999 14.26 15.7499 14.24L16.3699 13.99C16.4999 14.09 16.6399 14.17 16.7899 14.23L16.8799 14.89C16.8899 14.96 16.9399 15 16.9999 15H17.9999C18.0599 15 18.1199 14.96 18.1199 14.89L18.2099 14.23C18.3599 14.17 18.4999 14.08 18.6299 13.99L19.2499 14.24C19.3099 14.26 19.3699 14.24 19.3999 14.18L19.8999 13.31C19.9299 13.25 19.9199 13.19 19.8699 13.15L19.3499 12.74C19.3599 12.66 19.3699 12.58 19.3699 12.5C19.3699 12.42 19.3599 12.33 19.3499 12.26L19.8799 11.85C19.9299 11.81 19.9399 11.74 19.9199 11.68ZM17.4999 13.33C17.0399 13.33 16.6699 12.95 16.6699 12.5C16.6699 12.04 17.0499 11.67 17.4999 11.67C17.9499 11.67 18.3299 12.05 18.3299 12.5C18.3299 12.96 17.9599 13.33 17.4999 13.33Z"
          fill={fill}
        />
        <path
          d="M4.74 9H13.27C13.54 9 13.76 8.78 13.76 8.51V8.49C13.76 8.22 13.54 8 13.27 8H13C13 6.52 12.19 5.25 11 4.55V5.5C11 5.78 10.78 6 10.5 6C10.22 6 10 5.78 10 5.5V4.14C9.68 4.06 9.35 4 9 4C8.65 4 8.32 4.06 8 4.14V5.5C8 5.78 7.78 6 7.5 6C7.22 6 7 5.78 7 5.5V4.55C5.81 5.25 5 6.52 5 8H4.74C4.47 8 4.25 8.22 4.25 8.49V8.52C4.25 8.78 4.47 9 4.74 9Z"
          fill={fill}
        />
        <path d="M8.99965 13C10.8596 13 12.4096 11.72 12.8596 10H5.13965C5.58965 11.72 7.13965 13 8.99965 13Z" fill={fill} />
      </g>
      <defs>
        <clipPath id="clip0_176_690">
          <rect width="24" height="24" fill="white" />
        </clipPath>
      </defs>
    </svg>
  );
};



Engineering.propTypes = {
  /** custom width of the svg icon */
  width: PropTypes.string,
  /** custom height of the svg icon */
  height: PropTypes.string,
  /** custom colour of the svg icon */
  fill: PropTypes.string,
  /** custom class of the svg icon */
  className: PropTypes.string,
  /** custom style of the svg icon */
  style: PropTypes.object,
  /** Click Event handler when icon is clicked */
  onClick: PropTypes.func,
};
