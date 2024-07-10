# 快速迭代：如何使用 feature toggle 來開發佈署應用程式 - Aki wang Noah hsu

{%hackmd @DevOpsDay/B1cnefOI0 %}

#### 》[議程介紹](https://devopsdays.tw/2024/session-page/3098)
#### 》[填寫議程滿意度問卷｜回饋建言給辛苦的講者](https://forms.gle/mQ4xdPvPXzRWd7iTA)

> 共筆從這開始


###### tags: `DevOpsDays Taipei 2024`

### Prerequisite
- Docker Engine: 23+
- Java: JDK 11+ (recommand 17)
- sdk and sample code: [line/Flagship4j](https://github.com/line/Flagship4j)


### Demo step

1. 下載專案
sdk and sample code: [line/Flagship4j](https://github.com/line/Flagship4j)
(也可以看看專案中的其他 examples)

1.1 run flagr
```
docker compose up -d
```
access in `http://localhost:18000`

2. add code snippet in  `./examples/openfeature-spring-boot-example/build.gradle`

```
dependencies {
  ...
  implementation "org.springframework.boot:spring-boot-starter-web"
  ...
}
```

3. remove code from `OpenFeatureSpringBootExampleApplication`

```
 implements CommandLineRunner 
 
 public void run(String... args) throws Exception {
 ...}
```

4. add a class LayoutController


### throw Exception
```java
@RestController
public class LayoutController {

    @GetMapping("/layouts/product")
    public ResponseEntity<?> getLayoutProduct() {
        return ResponseEntity.ok(buildLayoutProduct());
    }

    private Map<?, ?> buildLayoutProduct() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
```

run app by IDE or command
```
./gradlew :examples:openfeature-spring-boot-example:bootRun
```

#### test it
```
curl --location -i 'http://localhost:8080/layouts/product'
```

### On / Off with not found
```java
@RestController
@RequiredArgsConstructor
public class LayoutController {

    private final Client flagrClient;

    @GetMapping("/layouts/product")
    public ResponseEntity<?> getLayoutProduct() {
        if (flagrClient.getBooleanValue("layout-prdocut-enabled", false)) {
            return ResponseEntity.ok(buildLayoutProduct());
        }

        return ResponseEntity.notFound().build();
    }

    private Map<?, ?> buildLayoutProduct() {
        return Map.of("btnBuyColor", "blue", "btnBuyRadius", "8px", "btnBuyPos", "page");
    }

}
```

### WhiteList
```java
@RestController
@RequiredArgsConstructor
public class LayoutController {

    private final Client flagrClient;

    @GetMapping("/layouts/product")
    public ResponseEntity<?> getLayoutProduct(@RequestHeader(name = "x-user-name", required = false) String userName) {
        MutableContext context = new MutableContext();
        if (userName != null && userName.equals("aki")) {
            context.add("entityContext", Structure.mapToStructure(Map.of("role", "ROLE_QA")));
        }

        if (flagrClient.getBooleanValue("layout-product-enabled", false, context)) {
            return ResponseEntity.ok(buildLayoutProduct());
        }

        return ResponseEntity.notFound().build();

    }

    private Map<?, ?> buildLayoutProduct() {
        return Map.of("btnBuyColor", "blue", "btnBuyRadius", "8px", "btnBuyPos", "page");
    }

}
```

#### test it
```
curl --location -i 'http://localhost:8080/layouts/product' \
-H "x-user-name:aki"
```

### A/B Testing 
```java
@RestController
@RequiredArgsConstructor
public class LayoutController {

    private final Client flagrClient;

    @GetMapping("/layouts/product")
    public ResponseEntity<?> getLayoutProduct(@RequestHeader(name = "x-user-name", required = false) String userName) {
        MutableContext context = new MutableContext();
        if (userName != null && userName.equals("aki")) {
            context.add("entityContext", Structure.mapToStructure(Map.of("role", "ROLE_QA")));
        }

        if (flagrClient.getBooleanValue("layout-product-enabled", false, context)) {
            String theme = flagrClient.getStringValue("layout-product-theme", "themeA", new MutableContext(userName));

            switch (theme) {
                case "themeA":
                    return ResponseEntity.ok(buildThemeALayoutProduct());
                case "themeB":
                    return ResponseEntity.ok(buildThemeBLayoutProduct());
                default:
                    return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.notFound().build();

    }

    private Map<?, ?> buildThemeALayoutProduct() {
        return Map.of("btnBuyColor", "blue", "btnBuyRadius", "8px", "btnBuyPos", "page");
    }

    private Map<?, ?> buildThemeBLayoutProduct() {
        return Map.of("btnBuyColor", "green", "btnBuyRadius", "0px", "btnBuyPos", "bottomNav");
    }

}
```

