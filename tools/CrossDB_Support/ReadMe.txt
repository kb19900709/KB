Description :

在 Spring 容器的環境下，使用客製化的 annotation 達到跨 DB Transaction 控制 
Multiple connections commit or roll back in the same time without JTA 

依賴於 Spring >>> TransactionManager & tx:annotation-driven & component-scan
PS:目前僅支援 PlatformTransactionManager 子類別，ex:HibernateTransactionManager
=============================================================================
Extra dependency in spring container: 

<!-- https://mvnrepository.com/artifact/org.aspectj/aspectjweaver -->
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.8.9</version>
</dependency>