# Neo4j Plugins

Instructions
------------
1. Build it (or use the provided jar file):
        mvn clean package
2. Copy XXX.jar to the plugins/ directory of your Neo4j server.
3. Configure Neo4j by adding a line to conf/neo4j.conf on version >= 3.0:
        dbms.unmanaged_extension_classes=com.sm.neo=/v1
4. (Re)Start Neo4j server 
5. Check that it is installed correctly over HTTP:
        :GET /v1/service/helloworld
6. Warm up the database (optional)
        :GET /v1/service/warmup
7. Query it:
        :POST /v1/connected {"relationships":["HAS_REGISTERED"]}  
        :GET /v1/connected/reset  
        :POST /v1/connected {"relationships":["HAS_REGISTERED", "HAS_PURCHASED"]}  
