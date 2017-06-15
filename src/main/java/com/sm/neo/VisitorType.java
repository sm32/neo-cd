package com.sm.neo;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;


/**
 The method counts subgraphs originating from new and terminating in return
 @param relationships
 */
@Path("/visitortype")
public class VisitorType {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @POST
    public Response searchToProperty(String body, @Context GraphDatabaseService db) throws IOException {
        HashMap input = Validators.getValidInput(body);
        LongAdder subgraphCount = new LongAdder(); //Connected subgraph ids
        LongAdder sviSubgraphCount = new LongAdder(); //Connected subgraph with svi id
        HashSet<Node> startNodeCollection = new HashSet(); //Connected subgraph with svi id
        HashSet<Node> endNodeCollection = new HashSet(); //Connected subgraph with svi id
        HashSet<Node> iteratorNode = new HashSet();
        HashSet<Node> nodeCollection = new HashSet();

        Transaction tx = db.beginTx();
        try {

            ArrayList<String> relationships = (ArrayList<String>) input.get("relationships");
            RelationshipType[] relationshipTypes = new RelationshipType[relationships.size()];
            for (int i = 0; i < relationships.size(); i ++) {
                relationshipTypes[i] = RelationshipType.withName(relationships.get(i));
            }

            TraversalDescription td = db.traversalDescription()
                    .breadthFirst()
                    .expand(new MultipleRelationshipExpander(relationshipTypes))
                    .evaluator(Evaluators.excludeStartPosition())
                    .uniqueness(Uniqueness.NODE_GLOBAL);

            ResourceIterator<Node> nodes = db.findNodes(Labels.Svi);

            while (nodes.hasNext()){
                Node node = nodes.next();

                boolean foundSvi = false;

                if (!iteratorNode.contains(node) && node.getProperty("visitor_type").toString().equals("RETURN")) {
                    iteratorNode.add(node);
                    subgraphCount.increment();

                    for (org.neo4j.graphdb.Path path : td.traverse(node)) {
                        iteratorNode.add(path.endNode());

                        if (path.endNode().hasLabel(Labels.Svi)) {
                            if (!foundSvi) {
                                foundSvi = true;
                                sviSubgraphCount.increment();
                            }

                            startNodeCollection.add(node);
                            endNodeCollection.add(path.endNode());

                            nodeCollection.add(node);
                            nodeCollection.add(path.endNode());
                        }
                    }

                    tx.success();
                }
                tx.close();
                tx = db.beginTx();

            }

        }
        catch ( Exception e ) {
            tx.failure();
        }
        finally {
            tx.close();
        }

        Map<String, Long> results = new HashMap<String,Long>();

        results.put("subgraph count", subgraphCount.longValue());
        results.put("subgraph with 2+ svi", sviSubgraphCount.longValue());
        results.put("svi count", Long.valueOf(nodeCollection.size()));
        results.put("end svi count", Long.valueOf(endNodeCollection.size()));
        results.put("start svi count", Long.valueOf(startNodeCollection.size()));

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();

    }

    @GET
    @Path("reset")
    public Response reset(@Context GraphDatabaseService db) throws IOException {

        Map<String, String> results = new HashMap<String,String>(){{
            put("empty function","no action");
        }};

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();

    }
}