package com.intelijake.mall.common.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@ConditionalOnProperty(prefix = "cloud.aws", name = "credentials.accessKey")
public class AWSUtil {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region}")
    private String region;

    /**
     * 确保bucket存在，如果不存在则创建
     */
    private void ensureBucketExists() {
        try {
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                System.out.println("Bucket " + bucketName + " does not exist. Creating bucket...");
                // 对于eu-west-1以外的区域，需要指定区域
                if ("us-east-1".equals(region)) {
                    amazonS3.createBucket(bucketName);
                } else {
                    CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, region);
                    amazonS3.createBucket(createBucketRequest);
                }
                System.out.println("Bucket " + bucketName + " created successfully.");
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Error creating bucket: " + ase.getMessage());
            // 如果bucket创建失败，但可能已经存在，继续执行
        }
    }

    public String uploadFile(String objectName, InputStream inputStream, long contentLength) throws Exception {
        //公网访问地址
        String url = "";
        try {
            // 确保bucket存在
            ensureBucketExists();

            // 创建ObjectMetadata对象
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);

            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, metadata);

            // 上传文件到S3
            amazonS3.putObject(putObjectRequest);

            // 构建S3文件的公网访问URL
            // https://bucket-name.s3.region.amazonaws.com/objectName
            url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + objectName;

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it to Amazon S3, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + ase.getMessage());
            System.out.println("HTTP Status Code:" + ase.getStatusCode());
            System.out.println("AWS Error Code:" + ase.getErrorCode());
            System.out.println("Error Type:" + ase.getErrorType());
            System.out.println("Request ID:" + ase.getRequestId());
            throw ase;
        } catch (SdkClientException sce) {
            System.out.println("Caught an SdkClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + sce.getMessage());
            throw sce;
        }
        return url;
    }

    public void deleteFile(String url) {
        try {
            // 从URL中提取对象名称
            String objectName = url.substring(url.lastIndexOf("/") + 1);

            // 创建删除请求
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectName);

            // 删除文件
            amazonS3.deleteObject(deleteObjectRequest);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it to Amazon S3, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + ase.getMessage());
            System.out.println("HTTP Status Code:" + ase.getStatusCode());
            System.out.println("AWS Error Code:" + ase.getErrorCode());
            System.out.println("Error Type:" + ase.getErrorType());
            System.out.println("Request ID:" + ase.getRequestId());
        } catch (SdkClientException sce) {
            System.out.println("Caught an SdkClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + sce.getMessage());
        }
    }
}