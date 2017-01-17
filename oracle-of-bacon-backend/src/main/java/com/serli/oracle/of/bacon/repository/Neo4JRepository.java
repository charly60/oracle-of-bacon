package com.serli.oracle.of.bacon.repository;


import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Pair;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.List;


public class Neo4JRepository {
    private final Driver driver;

    public Neo4JRepository() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pokemon2903"));
    }

    public List<String> getConnectionsToKevinBacon(String actorName) {
        Session session = driver.session();
        List<GraphItem> listresult = new ArrayList<GraphItem>();
        StatementResult result = session.run(
                "MATCH (Bacon:Actor {name:'Bacon, Kevin (I)'}), (Target:Actor {name:'"+
                        actorName +
                        "'}), p = shortestPath((Bacon)-[*]-(Target)) RETURN nodes(p) as nodes, relationships(p) as relations\n" );

        while ( result.hasNext() )
        {
            Record record = result.next();

            for (Value value:record.get("nodes").values()){
                Node node = value.asNode();
                GraphItem element ;
                if (node.containsKey("name")){
                    element = new GraphNode(node.id(),node.get("name").asString(),"Actor") ;
                }
                else {
                    element = new GraphNode(node.id(),node.get("title").asString(),"Movies");
                }
                listresult.add(element);

            }

            for (Value value:record.get("relations").values()){
                Relationship relation = value.asRelationship();
                GraphItem element = new GraphEdge(relation.id(),relation.startNodeId(),relation.endNodeId(),"PLAYED_IN") ;


                listresult.add(element);

            }
        }
        List<String> ls = new ArrayList<String>();
        for (int i =0; i<listresult.size(); i++){
            ls.add(transform(listresult.get(i)));
        }

        session.close();
        System.out.println(ls.toString());
        return ls;
    }

    private String transform(GraphItem g){
        String res = "";

        if (g instanceof GraphEdge){
            res +=  "{\n" +
                    "\"data\": {\n" +
                    "\"id\": " + ((GraphEdge) g).id + ",\n" +
                    "\"source\": " + ((GraphEdge) g).source + ",\n" +
                    "\"target\": " + ((GraphEdge) g).target + ",\n" +
                    "\"value\": \"PLAYED_IN\"\n" +
                    "}\n" +
                    "}";
        }
        if (g instanceof GraphNode){
            res +=  "{\n" +
                    "\"data\": {\n" +
                    "\"id\": " + ((GraphNode) g).id + ",\n" +
                    "\"type\": \""+((GraphNode) g).type+"\",\n" +
                    "\"value\": \""+ ((GraphNode) g).value +"\"\n" +
                    "}\n" +
                    "}\n";
        }

        return res;
    }


    private static abstract class GraphItem {
        public final long id;

        private GraphItem(long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GraphItem graphItem = (GraphItem) o;

            return id == graphItem.id;
        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }

    private static class GraphNode extends GraphItem {
        public final String type;
        public final String value;

        public GraphNode(long id, String value, String type) {
            super(id);
            this.value = value;
            if (type.equals("Movies"))
                type = "Movie";
            this.type = type;
        }
    }

    private static class GraphEdge extends GraphItem {
        public final long source;
        public final long target;
        public final String value;

        public GraphEdge(long id, long source, long target, String value) {
            super(id);
            this.source = source;
            this.target = target;
            this.value = value;
        }
    }
}
