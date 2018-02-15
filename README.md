[![Build Status](https://travis-ci.org/javadelight/delight-promise.svg)](https://travis-ci.org/javadelight/delight-promise)

# delight-promise

A basic promise implementation for Java.

## Usage

Creating a promise:

```java
Promise<Success> p1 = Promises.create(new Operation<Success>() {
	
	@Override
    public void apply(final ValueCallback<String> callback) {
    
    	// do work ...
    	
    	callback.onSuccess(Success.INSTANCE);
    }
});
```

Resolve promise:

```java
Success succ = p1.get();
// will throw a RuntimeException if promise cannot be resolved.
```

Resolving multiple promises in parallel:

```java
List<Success> res = Promises.parallel(p1, p2,  ...);
```

## Maven Dependency

```xml
<dependency>
    <groupId>org.javadelight</groupId>
    <artifactId>delight-promise</artifactId>
    <version>[insert latest version]</version>
</dependency>
```

This artifact is available on [Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cdelight-promise) and 
[BinTray](https://bintray.com/javadelight/javadelight/delight-promise).

[![Maven Central](https://img.shields.io/maven-central/v/org.javadelight/delight-promise.svg)](https://search.maven.org/#search%7Cga%7C1%7Cdelight-promise)

## Links

Part of [Java Delight](https://github.com/javadelight/delight-main#java-delight-suite).

[All Reports](http://modules.appjangle.com/delight-promise/latest/project-reports.html)


