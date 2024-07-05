// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'water_connection.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

WaterConnection _$WaterConnectionFromJson(Map<String, dynamic> json) =>
    WaterConnection()
      ..id = json['id'] as String?
      ..connectionNo = json['connectionNo'] as String?
      ..propertyId = json['propertyId'] as String?
      ..applicationNo = json['applicationNo'] as String?
      ..tenantId = json['tenantId'] as String?
      ..action = json['action'] as String?
      ..status = json['status'] as String?
      ..meterInstallationDate = json['meterInstallationDate'] as int?
      ..documents = json['documents'] == null
          ? null
          : Documents.fromJson(json['documents'] as Map<String, dynamic>)
      ..proposedTaps = json['proposedTaps'] as int?
      ..noOfTaps = json['noOfTaps'] as int?
      ..arrears = (json['arrears'] as num?)?.toDouble()
      ..connectionType = json['connectionType'] as String?
      ..ihlDetail = json['ihlDetail'] as String?
      ..sbmAccountno = json['sbmAccountno'] as String?
      ..oldConnectionNo = json['oldConnectionNo'] as String?
      ..meterId = json['meterId'] as String?
      ..propertyType = json['propertyType'] as String?
      ..previousReadingDate = json['previousReadingDate'] as int?
      ..previousReading = json['previousReading'] as int?
      ..proposedPipeSize = (json['proposedPipeSize'] as num?)?.toDouble()
      ..connectionHolders = (json['connectionHolders'] as List<dynamic>?)
          ?.map((e) => Owners.fromJson(e as Map<String, dynamic>))
          .toList()
      ..additionalDetails = json['additionalDetails'] == null
          ? null
          : AdditionalDetails.fromJson(
              json['additionalDetails'] as Map<String, dynamic>)
      ..processInstance = json['processInstance'] == null
          ? null
          : ProcessInstance.fromJson(
              json['processInstance'] as Map<String, dynamic>)
      ..paymentType = json['paymentType'] as String?
      ..penalty = (json['penalty'] as num?)?.toDouble()
      ..advance = (json['advance'] as num?)?.toDouble().abs();

Map<String, dynamic> _$WaterConnectionToJson(WaterConnection instance) =>
    <String, dynamic>{
      'id': instance.id,
      'connectionNo': instance.connectionNo,
      'propertyId': instance.propertyId,
      'applicationNo': instance.applicationNo,
      'tenantId': instance.tenantId,
      'action': instance.action,
      'status': instance.status,
      'meterInstallationDate': instance.meterInstallationDate,
      'documents': instance.documents,
      'proposedTaps': instance.proposedTaps,
      'noOfTaps': instance.noOfTaps,
      'arrears': instance.arrears,
      'connectionType': instance.connectionType,
      'ihlDetail':instance.ihlDetail,
      'sbmAccountno':instance.sbmAccountno,
      'oldConnectionNo': instance.oldConnectionNo,
      'meterId': instance.meterId,
      'propertyType': instance.propertyType,
      'previousReadingDate': instance.previousReadingDate,
      'previousReading': instance.previousReading,
      'proposedPipeSize': instance.proposedPipeSize,
      'connectionHolders': instance.connectionHolders,
      'additionalDetails': instance.additionalDetails,
      'processInstance': instance.processInstance,
      'paymentType': instance.paymentType,
      'penalty': instance.penalty,
      'advance': instance.advance,
    };

ProcessInstance _$ProcessInstanceFromJson(Map<String, dynamic> json) =>
    ProcessInstance()..action = json['action'] as String?;

Map<String, dynamic> _$ProcessInstanceToJson(ProcessInstance instance) =>
    <String, dynamic>{
      'action': instance.action,
    };

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..initialMeterReading = json['initialMeterReading'] is double
          ? json['initialMeterReading'].toInt()
          : json['initialMeterReading'] as int?
      ..meterReading = json['meterReading'] as int?
      ..locality = json['locality'] as String?
      ..category = json['category'] as String?
      ..subCategory = json['subCategory'] as String?
      ..aadharNumber = json['aadharNumber'] as String?
      ..propertyType = json['propertyType'] as String?
      ..street = json['street'] as String?
      ..lastDemandGeneratedDate = json['lastDemandGeneratedDate'] as String?
      ..doorNo = json['doorNo'] as String?
      ..collectionAmount = json['collectionAmount'] as String?
      ..collectionPendingAmount = json['collectionPendingAmount'] as String?
      ..action = json['action'] as String?
      ..totalAmount = json['totalamount'] as String?
      ..appCreatedDate = json['appCreatedDate'] as num?;

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) =>
    <String, dynamic>{
      'initialMeterReading': instance.initialMeterReading,
      'meterReading': instance.meterReading,
      'locality': instance.locality,
      'category': instance.category,
      'subCategory': instance.subCategory,
      'aadharNumber': instance.aadharNumber,
      'propertyType': instance.propertyType,
      'street': instance.street,
      'lastDemandGeneratedDate': instance.lastDemandGeneratedDate,
      'doorNo': instance.doorNo,
      'collectionAmount': instance.collectionAmount,
      'collectionPendingAmount': instance.collectionPendingAmount,
      'action': instance.action,
      'totalamount': instance.totalAmount,
      'appCreatedDate': instance.appCreatedDate,
    };
