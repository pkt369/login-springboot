# Login with spring boot
it is login example using spring boot. 
it can run with https://github.com/pkt369/login-nextjs.

## properties
To run this application, you must create a file named `application-local.properties`.
```properties
spring.security.oauth2.client.registration.google.client-id=
spring.security.oauth2.client.registration.google.client-secret=
spring.security.oauth2.client.registration.google.scope=

#JwtToken
jwt.secret-key=
jwt.expiration-hours=

spring.mail.host=
spring.mail.port=
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## profile
You should run this application with the `local` profile.

### Jar
To run this application with a JAR file, use:
```
java -jar your-app-name.jar --spring.profiles.active=local
```

### Default Setting in Properties
You can set it in `application.properties`:
```properties
spring.profiles.active=local
```

### IntelliJ Configuration
If you are using IntelliJ IDEA, you can set the profile in the Configuration tab.

<img width="292" alt="Image" src="https://github.com/user-attachments/assets/be8373c8-bb63-4070-8883-74a838f6bbbe" />

<img width="804" alt="Image" src="https://github.com/user-attachments/assets/92a47d9a-a6f1-4a81-b91c-72d6757685eb" />