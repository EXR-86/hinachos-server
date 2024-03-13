package com.elixr.hinachos.server.constants;

public class HiNachosQueryConstants {

    // Regular Constants
//    public static final String GET_REWARDS_SUMMARY_QUERY =
//            SELECT +
//                    "FROM (" +
//                    "    (" + GET_SENDERS_REWARDS_SUMMARY_QUERY + ") " + UNION_ALL +
//                    "    (" + GET_RECEIVERS_REWARDS_SUMMARY_QUERY + ")" +
//                    ") AS subquery " +
//                    ORDER_BY_TOTAL_REWARDS_COUNT_DESC;
    public static final String GET_RECEIVERS_REWARDS_SUMMARY_QUERY =
            "SELECT " +
                    "'receiver' AS type, " + // Specify 'receiver' as the type
                    "r.receiverId AS userId, " + // Include receiverId
                    "u.displayName AS displayName, " + // Include displayName from HiNachosUserDataEntity
                    "COUNT(r.rewardsCount) AS rewardsCount " + // Count rewardsCount
                    "FROM " +
                    "HiNachosRewardsDataEntity r " +
                    "JOIN " +
                    "HiNachosUserDataEntity u ON r.receiverId = u.userId " + // Join with HiNachosUserDataEntity
                    "GROUP BY " +
                    "r.receiverId, " + // Group by receiverId
                    "u.displayName"; // Group by displayName

    public static String WHERE_KEYWORD="WHERE";
    public static String AND_KEYWORD="AND";
    public static String CHANNELID ="channelId";
    public static String TAG_ID ="externalId";
    public static String STARTDATE ="startDate";
    public static String ENDDATE ="endDate";

    public static String CONDITION_TO_GET_TAG_DETAILS_BY_TAG_ID = " r.hashTagId = :"+HiNachosQueryConstants.TAG_ID + " ";
    public static final String WHERE_CLAUSE_PLACE_HOLDER = " #WHERE_CLAUSE_PLACE_HOLDER# ";
    public static final String UNION_ALL =  " UNION ALL ";
    public static final String GET_REWARDS_SUMMARY_RECEIVERS_QUERY_SQL ="select 'receiver' as type,  r.receiver_id, u.name, sum(r.rewards_count) as totalRewards " +
            "from exr_hinachos_rewards r" +
            "         JOIN exr_hinachos_user_details u ON r.receiver_id = u.external_id " +
            "group by r.receiver_id " +
            "order by totalRewards";

    public static final String GET_REWARDS_SUMMARY_SENDERS_QUERY_JPQL =
            "SELECT NEW com.elixr.hinachos.server.dto.HiNachosRewardsSummaryDataItem(" +
                    "ROW_NUMBER() OVER (ORDER BY COUNT(r.rewardsCount) DESC) , " + // rowNumber
                    "'sender', r.senderId, u.name, SUM(r.rewardsCount)) " +
                    "FROM HiNachosRewardsDataEntity r " +
                    "JOIN HiNachosUserDataEntity u ON r.senderId = u.externalId " +
                    WHERE_CLAUSE_PLACE_HOLDER +
                    "GROUP BY r.senderId, u.name " +
                    "ORDER BY SUM(r.rewardsCount) DESC";

    public static final String GET_REWARDS_SUMMARY_RECEIVERS_QUERY_JPQL =
            "SELECT NEW com.elixr.hinachos.server.dto.HiNachosRewardsSummaryDataItem" +
                    "(" +
                    "ROW_NUMBER() OVER (ORDER BY COUNT(r.rewardsCount) DESC) , " + // rowNumber
                    "'receiver', r.receiverId, u.name, SUM(r.rewardsCount)) " +
                    "FROM HiNachosRewardsDataEntity r " +
                    " JOIN HiNachosUserDataEntity u ON r.receiverId = u.externalId " +
                    WHERE_CLAUSE_PLACE_HOLDER +
                    " GROUP BY r.receiverId, u.name " +
                    " ORDER BY SUM(r.rewardsCount) DESC";

    public static final String GET_REWARDS_SUMMARY_SENDERS_AND_RECEIVERS_QUERY_JPQL = GET_REWARDS_SUMMARY_SENDERS_QUERY_JPQL + UNION_ALL + GET_REWARDS_SUMMARY_RECEIVERS_QUERY_JPQL;


    public static final String GET_REWARDS_SUMMARY_BY_HASHTAG_RECEIVERS_QUERY_JPQL =
            "SELECT NEW com.elixr.hinachos.server.dto.HiNachosRewardsSummaryDataItem" +
                    "(" +
                    "ROW_NUMBER() OVER (ORDER BY COUNT(r.rewardsCount) DESC) , " + // rowNumber
                    "'receiver', r.receiverId,  u.name, r.hashTagId, count(r.rewardsCount)) " +
                    "FROM HiNachosRewardsDataEntity r " +
                    "JOIN HiNachosHashTagDataEntity h ON r.hashTagId = h.externalId " +
                    "JOIN HiNachosUserDataEntity u ON r.receiverId = u.externalId " +
//                    "WHERE r.hashTagId IS NOT NULL " +
                    WHERE_CLAUSE_PLACE_HOLDER +
                    "GROUP BY r.hashTagId, r.receiverId, u.name " +
                    "ORDER BY count(r.rewardsCount) DESC";
    public static final String GET_SENDERS_REWARDS_SUMMARY_QUERY =
            "FROM (" +
                    "    SELECT " +
                    "        r.senderId AS userId, " +
                    "        r.senderDisplayName AS displayName, " +
                    "        SUM(r.rewardsCount) AS totalRewardsCount " +
                    "    FROM " +
                    "        HiNachosRewardsDataEntity r " +
                    "    JOIN " +
                    "        HiNachosUserDataEntity u ON r.senderId = u.externalId " +
                    " ";

    public static final String WHERE_CHANNEL_ID_EQUALS = "WHERE " +
            "        r.channelId = :channelId ";
    public static final String AND_TIME_OF_RECOGNITION_BETWEEN =   " AND r.timeOfRecognition BETWEEN :startDate AND :endDate ";
    public static final String ORDER_BY_TOTAL_REWARDS_COUNT_DESC =  ") AS subquery " +
            "ORDER BY " +
            "   totalRewardsCount DESC";



    public static final String GROUP_BY_SENDER_FIELDS =   " GROUP BY " +
            "r.hashTagId, r.senderId, r.senderDisplayName ";
    public static final String GROUP_BY_RECEIVER_FIELDS =     "GROUP BY " +
            " r.hashTagId, r.receiverId, r.receiverDisplayName ";

    public static final String SELECT = "SELECT " +
            "    ROW_NUMBER() OVER (ORDER BY totalRewardsCount DESC) AS recordNumber, " +
            "    'sender' AS role, " +
            "    userId, " +
            "    displayName, " +
            "    totalRewardsCount ";


}

