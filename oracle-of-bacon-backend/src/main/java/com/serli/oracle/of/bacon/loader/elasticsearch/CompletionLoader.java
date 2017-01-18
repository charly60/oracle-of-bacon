package com.serli.oracle.of.bacon.loader.elasticsearch;

import com.serli.oracle.of.bacon.repository.ElasticSearchRepository;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CompletionLoader {
    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Expecting 1 arguments, actual : " + args.length);
            System.err.println("Usage : completion-loader <actors file path>");
            System.exit(-1);
        }

        String inputFilePath = args[0];
        JestClient client = ElasticSearchRepository.createClient();
        Bulk.Builder builder = new Bulk.Builder()
                .defaultIndex("actor")
                .defaultType("name");


        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(inputFilePath))) {
            List<String> list = bufferedReader.lines().map(String::new).collect(Collectors.toCollection(ArrayList::new));
            System.out.println(list.size());
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
                String res = "{ \"name\" : " + list.get(i) + "}";
                builder.addAction(new Index.Builder(res).build());

                if (i % 10000 == 0) {
                    Bulk bulk = builder.build();
                        client.execute(bulk);
                        builder = new Bulk.Builder()
                                .defaultIndex("actor")
                                .defaultType("name");
                    System.out.println("10000 added");
                }
            }

            Bulk bulk = builder.build();
            client.execute(bulk);


            System.out.println(list.size() % 10000 + " added");


        }
        System.out.println("Inserted total of " + count.get() + " actors");
    }
}
