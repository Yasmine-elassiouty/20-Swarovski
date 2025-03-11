FROM openjdk:25-ea-4-jdk-oraclelinux9
WORKDIR /app
COPY ./ /app
EXPOSE 8080

ENV SPRING_APPLICATION_USERDATAPATH="/app/data/users.json"
ENV SPRING_APPLICATION_PRODUCTDATAPATH="/app/data/products.json"
ENV SPRING_APPLICATION_ORDERDATAPATH="/app/data/orders.json"
ENV SPRING_APPLICATION_CARTDATAPATH="/app/data/carts.json"

CMD ["java","-jar","/app/target/mini1.jar"]