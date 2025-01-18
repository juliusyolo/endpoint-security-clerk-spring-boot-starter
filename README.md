# Getting Started

### What is this

a springboot starter that integrates Clerk and Spring Security to secure API

### Maven dependency

```xml
<dependency>
    <groupId>com.juliusyolo</groupId>
    <artifactId>endpoint-security-clerk-spring-boot-starter</artifactId>
    <version>1.0.2-RELEASE</version>
</dependency>
```

### Features

- support reactive/servlet web endpoint security based on clerk and spring security
    - user authentication that uses clerk's authentication client
    - user authorization that bases on clerk's organization and role and permission
- just has no extra configuration