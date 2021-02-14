package ru.iteco.project.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /**
     * Название проекта
     */
    private String title;

    /**
     * Описание
     */
    private String description;

    /**
     * Контактное лицо
     */
    private Contact contract = new Contact();


    public SwaggerProperties(String title, String description, Contact contract) {
        this.title = title;
        this.description = description;
        this.contract = contract;
    }

    public SwaggerProperties() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Contact getContract() {
        return contract;
    }

    public void setContract(Contact contract) {
        this.contract = contract;
    }

    public static class Contact {

        private String name;

        private String url;

        private String mail;

        public Contact(String name, String url, String mail) {
            this.name = name;
            this.url = url;
            this.mail = mail;
        }

        public Contact() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }
    }
}
