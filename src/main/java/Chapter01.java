import redis.clients.jedis.Jedis;

/**
 * @author: 徐东强
 * @date: 2018/9/15 上午1:30
 * @description:
 **/
public class Chapter01 {

    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    private static final int VOTE_SCORE = 432;
    private static final int ARTICLES_PER_PAGE = 25;

    public static void main(String[] args) {

    }

    public void articleVote(Jedis conn, String user, String article){
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        if (conn.zscore("time:", article) < cutoff){
            return;
        }

    }
}
