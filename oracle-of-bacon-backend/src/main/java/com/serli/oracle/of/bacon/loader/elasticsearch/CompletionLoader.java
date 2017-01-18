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
import java.util.concurrent.atomic.AtomicInteger;

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


        ArrayList<Index> list = new ArrayList<>();

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(inputFilePath))) {
            bufferedReader.lines()
                    .forEach(line -> {
                        //TODO ElasticSearch insert
                        //System.out.println(line);
                        list.add(new Index.Builder(line).build());

                        if(list.size()>10000){
                            Bulk bulk = new Bulk.Builder()
                                    .defaultIndex("Actor")
                                    .defaultType("name")
                                    .addAction(list)
                                    .build();
                            try {
                                client.execute(bulk);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            list.clear();
                            System.out.println("10000 added");
                        }


                    });
            Bulk bulk = new Bulk.Builder()
                    .defaultIndex("Actor")
                    .defaultType("name")
                    .addAction(list)
                    .build();
            try {
                client.execute(bulk);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(list.size() + " added");
        }



        System.out.println("Inserted total of " + count.get() + " actors");
    }
}
