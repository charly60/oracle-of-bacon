package com.serli.oracle.of.bacon.api;

import com.serli.oracle.of.bacon.repository.ElasticSearchRepository;
import com.serli.oracle.of.bacon.repository.MongoDbRepository;
import com.serli.oracle.of.bacon.repository.Neo4JRepository;
import com.serli.oracle.of.bacon.repository.RedisRepository;
import net.codestory.http.annotations.Get;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class APIEndPoint {
    private final Neo4JRepository neo4JRepository;
    private final ElasticSearchRepository elasticSearchRepository;
    private final RedisRepository redisRepository;
    private final MongoDbRepository mongoDbRepository;

    public APIEndPoint() {
        neo4JRepository = new Neo4JRepository();
        elasticSearchRepository = new ElasticSearchRepository();
        redisRepository = new RedisRepository();
        mongoDbRepository = new MongoDbRepository();
    }

    @Get("bacon-to?actor=:actorName")
    public String getConnectionsToKevinBacon(String actorName) {
        redisRepository.addQuery(actorName);

        // target command in Neo4j
        //MATCH (Bacon:Actor {name:'Bacon, Kevin (I)'}), (Freeman:Actor {name:'Freeman, Morgan (I)'}), p = shortestPath((Bacon)-[*]-(Freeman)) RETURN p

        List<?> l = neo4JRepository.getConnectionsToKevinBacon(actorName);
        String res = "[\n";
        for (int i =0; i< l.size()-1;i++){
            res += l.get(i) + ", ";
        }
        res += l.get(l.size()-1);
        res += "]";

        return res;
    }

    @Get("suggest?q=:searchQuery")
    public List<String> getActorSuggestion(String searchQuery) throws IOException{


        return elasticSearchRepository.getActorsSuggests(searchQuery);
    }

    @Get("last-searches")
    public List<String> last10Searches() {



        return redisRepository.getLastTenSearches();
    }

    @Get("actor?name=:actorName")
    public String getActorByName(String actorName) {



        return "{\n" +
                "\"_id\": {\n" +
                "\"$oid\": \"587bd993da2444c943a25161\"\n" +
                "},\n" +
                "\"imdb_id\": \"nm0000134\",\n" +
                "\"name\": \"Robert De Niro\",\n" +
                "\"birth_date\": \"1943-08-17\",\n" +
                "\"description\": \"Robert De Niro, thought of as one of the greatest actors of all time, was born in Greenwich Village, Manhattan, New York City, to artists Virginia (Admiral) and Robert De Niro Sr. His paternal grandfather was of Italian descent, and his other ancestry is Irish, German, Dutch, English, and French. He was trained at the Stella Adler Conservatory and...\",\n" +
                "\"image\": \"https://images-na.ssl-images-amazon.com/images/M/MV5BMjAwNDU3MzcyOV5BMl5BanBnXkFtZTcwMjc0MTIxMw@@._V1_UY317_CR13,0,214,317_AL_.jpg\",\n" +
                "\"occupation\": [\n" +
                "\"actor\",\n" +
                "\"producer\",\n" +
                "\"soundtrack\"\n" +
                "]\n" +
                "}";
    }
}
