package com.sagark.ecommerce.project.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Builds the full image URL from the configured base URL and image name,
 * allowing the frontend to access and display the image correctly.
 */

@Component
public class ImageNameUtil {

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    public String constructImageUrl(String imageName){
        return imageBaseUrl.endsWith("/") ? imageBaseUrl+imageName : imageBaseUrl + "/" + imageName;
    }
}