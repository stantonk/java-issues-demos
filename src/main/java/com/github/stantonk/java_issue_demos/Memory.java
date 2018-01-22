package com.github.stantonk.java_issue_demos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sproutsocial.bus.client.BusSubscriber;
import com.sproutsocial.bus.config.GuiceMod;
import com.sproutsocial.nsq.Message;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Memory {

//    public void fetch(String urlStr) throws IOException, InterruptedException {
//        URL url = new URL(urlStr);
//        URLConnection uc = url.openConnection();
//        InputStream is = uc.getInputStream();
//        String result = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
//        System.out.println(result);
//    }
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//        while (true) {
//            Memory memory = new Memory();
//            memory.fetch("http://localhost:8000");
//        }
//    }
//}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TwitterBizMention {
        public String id;
        public String postedTime;
        public String link;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TwitterBizMention{");
            sb.append("id='").append(id).append('\'');
            sb.append(", postedTime='").append(postedTime).append('\'');
            sb.append(", link='").append(link).append('\'');
//            sb.append(", body='").append(body).append('\'');
//            sb.append(", objectType='").append(objectType).append('\'');
//            sb.append(", verb='").append(verb).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public String asJson() {
            return new Gson().toJson(this, TwitterBizMention.class);
        }
    }

    public static class PostConsumer {
        public static AtomicLong msgsConsumed = new AtomicLong(0);
        public static Map<String, TwitterBizMention> cache = new HashMap<>();
        private final Jdbi jdbi;

        public PostConsumer(Jdbi jdbi) {
            this.jdbi = jdbi;
        }

        public void consume(Message m) {
            Gson gson = new Gson();
            System.out.println("size of cache = " + cache.size());
            try {
                TwitterBizMention twitterBizMention = gson.fromJson(new String(m.getData()), TwitterBizMention.class);
                m.finish();
                if (cache.get(twitterBizMention.id) == null) {
                    System.out.println(String.format("%s: %s", msgsConsumed.incrementAndGet(), twitterBizMention.asJson()));
                    cache.put(twitterBizMention.id, twitterBizMention);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        final Injector injector = Guice.createInjector(new GuiceMod.DefaultBusModule());
        BusSubscriber subscriber = injector.getInstance(BusSubscriber.class);
        subscriber.startAsync();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:13306/bizeebe");
        ds.setUsername("root");
        ds.setPassword("");
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setMaximumPoolSize(4);
        Jdbi jdbi = Jdbi.create(ds);

        PostConsumer postConsumer = new PostConsumer(jdbi);
        subscriber.getRawSubscriber().subscribe(
                "raw_tw_biz_mention_json",
                "memory#ephemeral",
                postConsumer::consume
        );


    }
}
