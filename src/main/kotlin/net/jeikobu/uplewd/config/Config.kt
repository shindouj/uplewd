package net.jeikobu.uplewd.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver

@Configuration
class Config {

    @Value("\${UPLEWD_GLOBAL_UPLOAD_LIMIT}")
    var maxFileSizeMB: Long = 250
    @Bean
    fun multipartResolver(): MultipartResolver {
        return CommonsMultipartResolver().apply {
            setMaxUploadSize(maxFileSizeMB * 1024 * 1024)
        }
    }
}