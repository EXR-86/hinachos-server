package com.elixr.hinachos.server.service;

import com.elixr.hinachos.server.constants.HiNachosServerConstants;
import com.elixr.hinachos.server.dao.HiNachosDao;
import com.elixr.hinachos.server.persistence.domain.*;
import com.elixr.hinachos.server.dto.*;
import com.elixr.hinachos.server.request.*;
import com.elixr.hinachos.server.response.HiNachosCustomLabelsResponse;
import com.elixr.hinachos.server.response.HiNachosServerBaseResponse;
import com.elixr.hinachos.server.response.HiNachosTagsResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.elixr.hinachos.server.constants.HiNachosServerConstants.DISPLAY_TABLE_PAGE_SIZE;

@Service
@Slf4j
public class HiNachosService {

    private final HiNachosDao hiNachosDao;

    @Autowired
    private EntityManager entityManager;

    @Value("${com.exr.application_display_name}")
    private String applicationDisplayName;
    public HiNachosService(HiNachosDao hiNachosDao) {
        this.hiNachosDao = hiNachosDao;
    }

    /**
     * Fetch reward details based on the search criteria.
     *
     * @param hiNachosRewardsSearchCriteria The search criteria for fetching rewards.
     * @return The details of the rewards.
     * @throws Exception if an error occurs.
     */
    public HiNachosRewardsDataWrapper getRewardDetails(HiNachosRewardsSearchCriteria hiNachosRewardsSearchCriteria) throws Exception {
        HiNachosRewardsDataWrapper hinachosRewardsDataWrapper = new HiNachosRewardsDataWrapper();
        String rewardedUser = hiNachosRewardsSearchCriteria.getUserId();
        List<HiNachosRewardsDataEntity> heyNachosRewardsItemDataList;
        heyNachosRewardsItemDataList = getRewards(hiNachosRewardsSearchCriteria.getUserId(), hiNachosRewardsSearchCriteria.getSenderId(), hiNachosRewardsSearchCriteria.getRedeemedOption(),
                hiNachosRewardsSearchCriteria.getStartDate(),
                hiNachosRewardsSearchCriteria.getEndDate(), (int) hiNachosRewardsSearchCriteria.getPageIndex());
        List<HiNachosRewardsDataItem> hiNachosRewardsDataItemList = mapRewardsDataENtityListToDataItemList(heyNachosRewardsItemDataList);
        hinachosRewardsDataWrapper.setTotalRewardsCount(calculateRewardCounts(heyNachosRewardsItemDataList));
        hinachosRewardsDataWrapper.setUserName(rewardedUser);
        hinachosRewardsDataWrapper.setHeyNachosRewardsDataItemList(hiNachosRewardsDataItemList);
        return hinachosRewardsDataWrapper;
    }

    /**
     * Assign rewards to user.
     *
     * @param hiNachosRewardAssignmentRequest The request containing details for assigning rewards.
     * @return The server response after assigning rewards.
     * @throws Exception if an error occurs.
     */
    public HiNachosServerBaseResponse assignRewardsToUser(HiNachosRewardAssignmentRequest hiNachosRewardAssignmentRequest) throws Exception {
        hiNachosRewardAssignmentRequest.filterUniqueReceiverIds();
        Date recognitionDate = new Date();
        String senderName = hiNachosRewardAssignmentRequest.getSenderDataItem().getDisplayName();
        StringBuilder recognitionMessage = new StringBuilder();
        HiNachosServerBaseResponse heyNachosServerSuccessResponse = new HiNachosServerBaseResponse();
        HiNachosRewardsDataEntity hiNachosRewardsDataEntity;
//        heyNachosServerSuccessResponse.setTimeWhenMessageReceived(getFormattedDateValue(recognitionDate));
        if (ObjectUtils.isEmpty(hiNachosRewardAssignmentRequest.getSenderDataItem())) {
            heyNachosServerSuccessResponse.setMessage("Sender details is not present. Unable to proceed");
            heyNachosServerSuccessResponse.setSuccess(false);
            heyNachosServerSuccessResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            log.warn("Aborting the operation as sender is undefined");
            return heyNachosServerSuccessResponse;
        }
        if (ObjectUtils.isEmpty(hiNachosRewardAssignmentRequest.getReceiverDataItemList())) {
            heyNachosServerSuccessResponse.setMessage("Receiver details is not present. Unable to proceed");
            heyNachosServerSuccessResponse.setSuccess(false);
            heyNachosServerSuccessResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            log.warn("Aborting the operation as receiver is undefined");
            return heyNachosServerSuccessResponse;
        }
        if (ObjectUtils.isEmpty(hiNachosRewardAssignmentRequest.getRewardDetails().getRecognitionMessage())) {
            heyNachosServerSuccessResponse.setMessage("Received empty message, from user:- " + senderName);
            heyNachosServerSuccessResponse.setSuccess(false);
            heyNachosServerSuccessResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            log.warn("Aborting the operation due to empty message body from user:- " + senderName);
            return heyNachosServerSuccessResponse;
        }
       String hashTag =  extractAndSaveHashtags(hiNachosRewardAssignmentRequest.getRewardDetails().getRecognitionMessage());
        String message = hiNachosRewardAssignmentRequest.getRewardDetails().getRecognitionMessage();
        String remainder = message.replaceAll(HiNachosServerConstants.REGEX_TO_SUBTRACT_STRING_FROM_MESSAGE, HiNachosServerConstants.EMPTY_STRING);
        recognitionMessage.append(remainder.trim());
        String recognitionMessageString = recognitionMessage.toString().trim();
        List<String> receiverNamesList = new ArrayList<>();
        String receiverNames;
        String senderId = hiNachosRewardAssignmentRequest.getSenderDataItem().getId();
        String senderDisplayName = hiNachosRewardAssignmentRequest.getSenderDataItem().getDisplayName();

        if(!ObjectUtils.isEmpty(hiNachosRewardAssignmentRequest.getChannelDetails())) {
            HiNachosChannelDataEntity hiNachosChannelDataEntity = new HiNachosChannelDataEntity();
            hiNachosChannelDataEntity.setName(hiNachosRewardAssignmentRequest.getChannelDetails().getChannelName());
            hiNachosChannelDataEntity.setExternalId(hiNachosRewardAssignmentRequest.getChannelDetails().getChannelId());
            saveChannelDetails(hiNachosChannelDataEntity);
        }

        HiNachosUserDataEntity hiNachosUserDataEntityForSender = new HiNachosUserDataEntity();
        hiNachosUserDataEntityForSender.setExternalId(hiNachosRewardAssignmentRequest.getSenderDataItem().getId());
        hiNachosUserDataEntityForSender.setName(senderName);
        hiNachosUserDataEntityForSender.setFirstName(hiNachosRewardAssignmentRequest.getSenderDataItem().getFirstName());
        hiNachosUserDataEntityForSender.setLastName(hiNachosRewardAssignmentRequest.getSenderDataItem().getLastName());
        hiNachosUserDataEntityForSender.setEmailId(hiNachosRewardAssignmentRequest.getSenderDataItem().getEmailId());
        saveUserDetails(hiNachosUserDataEntityForSender);

        for (HiNachosRewardAssignmentRequest.UserDataItem receiverDetails : hiNachosRewardAssignmentRequest.getReceiverDataItemList()) {
            hiNachosRewardsDataEntity = new HiNachosRewardsDataEntity();
            hiNachosRewardsDataEntity.setSenderId(senderId);
            HiNachosUserDataEntity hiNachosUserDataEntityForReciever = new HiNachosUserDataEntity();

            String receiverName = receiverDetails.getDisplayName();
            hiNachosUserDataEntityForReciever.setExternalId(receiverDetails.getId());
            hiNachosUserDataEntityForReciever.setName(receiverDetails.getDisplayName());
            hiNachosUserDataEntityForReciever.setFirstName(receiverDetails.getFirstName());
            hiNachosUserDataEntityForReciever.setLastName(receiverDetails.getLastName());
            hiNachosUserDataEntityForReciever.setEmailId(receiverDetails.getEmailId());

            hiNachosRewardsDataEntity.setReceiverId(receiverDetails.getId());
            hiNachosRewardsDataEntity.setRecognitionMessage(recognitionMessageString);
            hiNachosRewardsDataEntity.setTimeOfRecognition(recognitionDate);
            hiNachosRewardsDataEntity.setChannelId(hiNachosRewardAssignmentRequest.getChannelDetails() != null
                    ? hiNachosRewardAssignmentRequest.getChannelDetails().getChannelId() : null);

            hiNachosRewardsDataEntity.setRedeemed(HiNachosServerConstants.REDEEMED_OPTION_NO);
            hiNachosRewardsDataEntity.setRewardsCount(hiNachosRewardAssignmentRequest.getRewardDetails().getEmojisList().size());
            hiNachosRewardsDataEntity.setHashTagId(!ObjectUtils.isEmpty(hashTag) ? hashTag : null);

            saveUserDetails(hiNachosUserDataEntityForReciever);
            hiNachosDao.saveRewardDetails(hiNachosRewardsDataEntity);

            receiverNamesList.add(receiverName);
            log.info("Assigned rewards to user:- " + receiverName + ", sent by " + senderName + " with message:- " + recognitionMessageString);
        }

        receiverNames = receiverNamesList.toString();
        receiverNames = receiverNames.replace(HiNachosServerConstants.SQUARE_BRACKET_OPEN, HiNachosServerConstants.EMPTY_STRING).
                replace(HiNachosServerConstants.SQUARE_BRACKET_CLOSE, HiNachosServerConstants.EMPTY_STRING) ;
        heyNachosServerSuccessResponse.setMessage(receiverNames + " received rewards from:- " + senderName + ": " + recognitionMessageString);
        heyNachosServerSuccessResponse.setSuccess(true);
        heyNachosServerSuccessResponse.setStatusCode(HttpStatus.OK.value());
        return heyNachosServerSuccessResponse;
    }

    private String extractAndSaveHashtags(String recognitionMessage) {
        boolean valueHasChanged = false;
        List<HiNachosHashTagDataEntity> hiNachosHashTagDataEntityList = new ArrayList<>();
        List<String> hashtagsList = extractHashtags(recognitionMessage);
        if (!hashtagsList.isEmpty()) {
            HiNachosHashTagDataEntity hiNachosHashTagDataEntityFromDB = hiNachosDao.getTagById(hashtagsList.get(0));
            String hashTag = hashtagsList.get(0);
            if (hiNachosHashTagDataEntityFromDB != null) {
                entityManager.detach(hiNachosHashTagDataEntityFromDB);
                if (!Objects.equals(hashTag, hiNachosHashTagDataEntityFromDB.getExternalId())) {
                    if (StringUtils.hasText(hashTag)) {
                        hiNachosHashTagDataEntityFromDB.setExternalId(hashTag);
                        valueHasChanged = true;
                    }
                }
                if (!Objects.equals(hashTag, hiNachosHashTagDataEntityFromDB.getName())) {
                    if (StringUtils.hasText(hashTag)) {
                        hiNachosHashTagDataEntityFromDB.setName(hashTag);
                        valueHasChanged = true;
                    }
                }
            } else {
                hiNachosHashTagDataEntityFromDB = new HiNachosHashTagDataEntity();
                hiNachosHashTagDataEntityFromDB.setName(hashTag);
                hiNachosHashTagDataEntityFromDB.setId(UUID.randomUUID().toString());
                hiNachosHashTagDataEntityFromDB.setExternalId(hashTag);
                valueHasChanged = true;
            }
            if (valueHasChanged) {
                hiNachosDao.saveHashtags(hiNachosHashTagDataEntityFromDB);
            }
            return hashtagsList.get(0);
        }
            return "";
    }

    /**
     * Retrieve rewards based on search criteria.
     *
     * @param receiverId     The ID of the receiver.
     * @param senderId     The ID of the sender.
     * @param isRedeemed The redemption status of the rewards.
     * @param startDate  The start date for filtering rewards.
     * @param endDate    The end date for filtering rewards.
     * @param pageNumber The page number for pagination.
     * @return The list of rewards based on the search criteria.
     */
    private List<HiNachosRewardsDataEntity> getRewards(String receiverId, String senderId, String isRedeemed, Date startDate, Date endDate, int pageNumber) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<HiNachosRewardsDataEntity> query = builder.createQuery(HiNachosRewardsDataEntity.class);
        Root<HiNachosRewardsDataEntity> root = query.from(HiNachosRewardsDataEntity.class);
        query.select(root);

        List<Predicate> predicates = createPredicatesForRetrievalQuery(builder, root, receiverId, senderId, isRedeemed,
                startDate,
                endDate);

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        int firstResult = (pageNumber - 1) * DISPLAY_TABLE_PAGE_SIZE;
        List<HiNachosRewardsDataEntity> hiNachosRewardsDataList = entityManager.createQuery(query)
                .setFirstResult(firstResult)
                .setMaxResults(DISPLAY_TABLE_PAGE_SIZE)
                .getResultList();
        log.info("Generated SQL query: " + entityManager.createQuery(query).unwrap(org.hibernate.query.Query.class).getQueryString());

        return hiNachosRewardsDataList;
    }

    /**
     * Create predicates for the retrieval query based on search criteria.
     *
     * @param builder    The criteria builder.
     * @param root       The root of the criteria query.
     * @param receiverId     The ID of the user.
     * @param isRedeemed The redemption status of the rewards.
     * @param startDate  The start date for filtering rewards.
     * @param endDate    The end date for filtering rewards.
     * @return The list of predicates for the retrieval query.
     */
    private List<Predicate> createPredicatesForRetrievalQuery(CriteriaBuilder builder, Root<HiNachosRewardsDataEntity> root, String receiverId, String senderId,
                                                              String isRedeemed, Date startDate, Date endDate) {
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.hasText(receiverId)) {
            predicates.add(builder.equal(root.get(HiNachosServerConstants.FIELD_RECEIVER_ID), receiverId));
        }
        if (StringUtils.hasText(senderId)) {
            predicates.add(builder.equal(root.get(HiNachosServerConstants.FIELD_SENDER_ID), senderId));
        }
        if (startDate != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(HiNachosServerConstants.FIELD_TIME_OF_RECOGNITION),  new Timestamp(startDate.getTime())));
        }
        if (endDate != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(HiNachosServerConstants.FIELD_TIME_OF_RECOGNITION),  new Timestamp(endDate.getTime())));
        }
        if(!ObjectUtils.isEmpty(isRedeemed)) {
            if (isRedeemed.equals(HiNachosServerConstants.REDEEMED_OPTION_NO)) {
                predicates.add(builder.equal(root.get(HiNachosServerConstants.FIELD_REDEEMED), isRedeemed));
            } else if (isRedeemed.equals(HiNachosServerConstants.REDEEMED_OPTION_YES)) {
                predicates.add(builder.equal(root.get(HiNachosServerConstants.FIELD_REDEEMED), isRedeemed));
            }
        }
        return predicates;
    }

    /**
     * Map rewards data to sender rewards data list.
     *
     * @param hiNachosRewardsDataList The list of rewards data.
     * @return The list of sender rewards data.
     */
    public List<HiNachosRewardsDataItem> mapRewardsDataENtityListToDataItemList(List<HiNachosRewardsDataEntity> hiNachosRewardsDataList) {
        List<HiNachosRewardsDataItem> hiNachosRewardsDataItemList = hiNachosRewardsDataList.stream()
                .map(this::mapRewardsDataEntityToDataItem)
                .collect(Collectors.toList());

        return hiNachosRewardsDataItemList;
    }

    /**
     * Map rewards data to sender rewards data.
     *
     * @param hiNachosRewardsDataEntity The rewards data to map.
     * @return The sender rewards data.
     */
    private HiNachosRewardsDataItem mapRewardsDataEntityToDataItem(HiNachosRewardsDataEntity hiNachosRewardsDataEntity) {
        String senderName = hiNachosDao.getUserByUserId(hiNachosRewardsDataEntity.getSenderId()).getName();
        String receiverName = hiNachosDao.getUserByUserId(hiNachosRewardsDataEntity.getReceiverId()).getName();
        HiNachosRewardsDataItem hiNachosRewardsDataItem = new HiNachosRewardsDataItem();
        hiNachosRewardsDataItem.setSenderId(hiNachosRewardsDataEntity.getSenderId());
        hiNachosRewardsDataItem.setSenderName(senderName);
        hiNachosRewardsDataItem.setReceiverId(hiNachosRewardsDataEntity.getReceiverId());
        hiNachosRewardsDataItem.setReceiverName(receiverName);
        hiNachosRewardsDataItem.setTotalRewardsCount(hiNachosRewardsDataEntity.getRewardsCount());
        hiNachosRewardsDataItem.setTimeOfRecognition(getFormattedDateValue(hiNachosRewardsDataEntity.getTimeOfRecognition()));
        return hiNachosRewardsDataItem;
    }

    private String getFormattedDateValue(Date dateObject) {
        String formattedDateString = "";
        if(dateObject != null) {
            SimpleDateFormat simpleDateFormatForResponse = new SimpleDateFormat(HiNachosServerConstants.DATE_FORMAT_IN_YYMMDD_HH_MM_SECONDS);
            formattedDateString = simpleDateFormatForResponse.format(dateObject);
        }
        return formattedDateString;
    }
    /**
     * Calculate reward counts based on the list of rewards data.
     *
     * @param hiNachosRewardsDataList The list of rewards data.
     * @return The reward counts.
     */
    private RewardCounts calculateRewardCounts(List<HiNachosRewardsDataEntity> hiNachosRewardsDataList) {
        RewardCounts rewardCounts = new RewardCounts();
        long totalCount = 0;
        long totalRedeemedCount = 0;
        long totalNonRedeemedCount = 0;
        for (HiNachosRewardsDataEntity hiNachosRewardsDataEntity : hiNachosRewardsDataList) {
            // If all receiverIds are the same, proceed with counting rewards based on redeemed value
            if (hiNachosRewardsDataEntity.getRedeemed().equals(HiNachosServerConstants.REDEEMED_OPTION_YES)) {
                totalCount += hiNachosRewardsDataEntity.getRewardsCount();
                totalRedeemedCount += hiNachosRewardsDataEntity.getRewardsCount();
            }
            if (hiNachosRewardsDataEntity.getRedeemed().equals(HiNachosServerConstants.REDEEMED_OPTION_NO)) {
                totalCount += hiNachosRewardsDataEntity.getRewardsCount();
                totalNonRedeemedCount += hiNachosRewardsDataEntity.getRewardsCount();
            }
        }
        rewardCounts.setTotalCount(totalCount);
        rewardCounts.setRedeemedYCount(totalRedeemedCount);
        rewardCounts.setRedeemedNCount(totalNonRedeemedCount);
        return rewardCounts;
    }

    public void saveUserDetails(HiNachosUserDataEntity hiNachosUserDataEntity) {
        HiNachosUserDataEntity hiNachosUserDataEntityFromDB = hiNachosDao.getUserByUserId(hiNachosUserDataEntity.getExternalId());
        boolean valueHasChanged = false;
        if(hiNachosUserDataEntityFromDB != null) {
            entityManager.detach(hiNachosUserDataEntityFromDB);
            if (!Objects.equals(hiNachosUserDataEntity.getEmailId(), hiNachosUserDataEntityFromDB.getEmailId())) {
                if (StringUtils.hasText(hiNachosUserDataEntity.getEmailId())) {
                    hiNachosUserDataEntityFromDB.setEmailId(hiNachosUserDataEntity.getEmailId());
                    valueHasChanged = true;
                }
            }
            if (!Objects.equals(hiNachosUserDataEntity.getName(), hiNachosUserDataEntityFromDB.getName())) {
                if (StringUtils.hasText(hiNachosUserDataEntity.getName())) {
                    hiNachosUserDataEntityFromDB.setName(hiNachosUserDataEntity.getName());
                    valueHasChanged = true;
                }
            }
            if (!Objects.equals(hiNachosUserDataEntity.getFirstName(), hiNachosUserDataEntityFromDB.getFirstName())) {
                if (StringUtils.hasText(hiNachosUserDataEntity.getFirstName())) {
                    hiNachosUserDataEntityFromDB.setFirstName(hiNachosUserDataEntity.getFirstName());
                    valueHasChanged = true;
                }
            }
            if (!Objects.equals(hiNachosUserDataEntity.getLastName(), hiNachosUserDataEntityFromDB.getLastName())) {
                if (StringUtils.hasText(hiNachosUserDataEntity.getLastName())) {
                    hiNachosUserDataEntityFromDB.setLastName(hiNachosUserDataEntity.getLastName());
                    valueHasChanged = true;
                }
            }
        } else {
            hiNachosUserDataEntityFromDB = hiNachosUserDataEntity;
            valueHasChanged = true;
        }
        if (valueHasChanged) {
            hiNachosDao.saveUserDetails(hiNachosUserDataEntityFromDB);
        }
    }

    public void saveChannelDetails(HiNachosChannelDataEntity hiNachosChannelDataEntity) {
        HiNachosChannelDataEntity hiNachosUserDataEntityFromDB = hiNachosDao.getChannelByChannelId(hiNachosChannelDataEntity.getExternalId());
        boolean valueHasChanged = false;
        if(hiNachosUserDataEntityFromDB != null) {
            entityManager.detach(hiNachosUserDataEntityFromDB);
            if (!Objects.equals(hiNachosChannelDataEntity.getExternalId(), hiNachosUserDataEntityFromDB.getExternalId())) {
                if (StringUtils.hasText(hiNachosChannelDataEntity.getExternalId())) {
                    hiNachosUserDataEntityFromDB.setExternalId(hiNachosChannelDataEntity.getExternalId());
                    valueHasChanged = true;
                }
            }
            if (!Objects.equals(hiNachosChannelDataEntity.getName(), hiNachosUserDataEntityFromDB.getName())) {
                if (StringUtils.hasText(hiNachosChannelDataEntity.getName())) {
                    hiNachosUserDataEntityFromDB.setName(hiNachosChannelDataEntity.getName());
                    valueHasChanged = true;
                }
            }
        } else {
            hiNachosUserDataEntityFromDB = hiNachosChannelDataEntity;
            valueHasChanged = true;
        }
        if (valueHasChanged) {
            hiNachosDao.saveChannelDetails(hiNachosUserDataEntityFromDB);
        }
    }

    public List<HiNachosRewardsSummaryDataItem> getTopUsers(HiNachosRewardsSummarySearchCriteria hiNachosRewardsSummarySearchCriteria) throws Exception {
        List<HiNachosRewardsSummaryDataItem> hiNachosRewardsDataEntityList = new ArrayList<>();
        hiNachosRewardsDataEntityList = hiNachosDao.getTopUsers(hiNachosRewardsSummarySearchCriteria);
        return hiNachosRewardsDataEntityList;
    }

    public HiNachosTagsResponse getTagDetails(HiNachosTagsSearchCriteria hiNachosTagsSearchCriteria) throws Exception{
        HiNachosTagsResponse hiNachosTagsResponse = new HiNachosTagsResponse();
        List<HiNachosRewardsSummaryDataItem> resultList = hiNachosDao.getTagDetails(hiNachosTagsSearchCriteria);
        Map<String, List<HiNachosRewardsSummaryDataItem>> groupedResult = resultList.stream()
                .collect(Collectors.groupingBy(HiNachosRewardsSummaryDataItem::getHashTagId));

        groupedResult.forEach((hashTagId, itemList) -> {
            // Sort the list in descending order based on totalRewards
            itemList.sort(Comparator.comparingLong(HiNachosRewardsSummaryDataItem::getTotalRewards).reversed());

            // Update the rank values based on the sorted order
            IntStream.range(0, itemList.size())
                    .forEach(i -> itemList.get(i).setRank(i + 1)); // Update rank starting from 1
        });
        hiNachosTagsResponse.setHiNachosTagDetailsMap(groupedResult);
        return hiNachosTagsResponse;
    }

    private static List<String> extractHashtags(String text) {
        List<String> hashtags = new ArrayList<>();

        // Regular expression to match hashtags (# followed by one or more alphanumeric characters)
        Pattern pattern = Pattern.compile(HiNachosServerConstants.REGEX_TO_SUBTRACT_HASHTAGS_FROM_MESSAGE);
        Matcher matcher = pattern.matcher(text);

        // Find hashtags and add them to the list
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }

        return hashtags;
    }

    public HiNachosServerBaseResponse updateCustomLabels(HiNachosCustomNameRegistryRequest hiNachosCustomNameRegistryRequest)throws Exception {
        HiNachosCustomLabelsDataEntity hiNachosCustomLabelsDataRecentEntity = new HiNachosCustomLabelsDataEntity();
        HiNachosServerBaseResponse hiNachosServerBaseResponse = new HiNachosServerBaseResponse();
        if (ObjectUtils.isEmpty(hiNachosCustomNameRegistryRequest.getDomainName())) {
            hiNachosServerBaseResponse.setMessage("Domain name is not specified, Unable to proceed");
            return hiNachosServerBaseResponse;
        }
        if (hiNachosCustomNameRegistryRequest.getHiNachosCustomNameRegistryMap() == null ||
                hiNachosCustomNameRegistryRequest.getHiNachosCustomNameRegistryMap().isEmpty()) {
            hiNachosServerBaseResponse.setMessage("No data found to save customization, Unable to proceed");
            return hiNachosServerBaseResponse;
        }
        hiNachosCustomLabelsDataRecentEntity.setDomainName(hiNachosCustomNameRegistryRequest.getDomainName());
        String customLabelsJsonString = new Gson().toJson(hiNachosCustomNameRegistryRequest.getHiNachosCustomNameRegistryMap());
        hiNachosCustomLabelsDataRecentEntity.setCustomLabelsJsonString(customLabelsJsonString);

        HiNachosCustomLabelsDataEntity hiNachosCustomLabelsDataEntityFromDB = hiNachosDao.getCustomLabelByDomain(hiNachosCustomLabelsDataRecentEntity.getDomainName());
        if (hiNachosCustomLabelsDataEntityFromDB != null) {
            hiNachosCustomLabelsDataEntityFromDB.setCustomLabelsJsonString(hiNachosCustomLabelsDataRecentEntity.getCustomLabelsJsonString());
            hiNachosDao.saveCustomLabelDetails(hiNachosCustomLabelsDataEntityFromDB);
        } else {
            hiNachosDao.saveCustomLabelDetails(hiNachosCustomLabelsDataRecentEntity);
        }
        hiNachosServerBaseResponse.setMessage("Successfully saved customization details");
        return hiNachosServerBaseResponse;
    }

    public HiNachosCustomLabelsResponse getAllCustomLabels()throws Exception {
        List<HiNachosCustomLabelsResponse.HiNachosCustomLabelsResponseAttributes> hiNachosCustomLabelsResponseAttributesList = new ArrayList<>();
        HiNachosCustomLabelsResponse hiNachosCustomLabelsResponse = new HiNachosCustomLabelsResponse();
        List<HiNachosCustomLabelsDataEntity> hiNachosCustomLabelsDataEntityList = hiNachosDao.getAllCustomLabels();
        for (HiNachosCustomLabelsDataEntity hiNachosCustomLabelsDataEntity : hiNachosCustomLabelsDataEntityList) {
            HiNachosCustomLabelsResponse.HiNachosCustomLabelsResponseAttributes hiNachosCustomLabelsResponseAttributes =
                    new HiNachosCustomLabelsResponse.HiNachosCustomLabelsResponseAttributes();
            hiNachosCustomLabelsResponseAttributes.setHiNachosCustomLabelsDataMapList(!hiNachosCustomLabelsDataEntityList.isEmpty() ?
                    parseCustomLabelsJsonToMap(hiNachosCustomLabelsDataEntity.getCustomLabelsJsonString()) : null);
            hiNachosCustomLabelsResponseAttributes.setDomainName(hiNachosCustomLabelsDataEntity.getDomainName());
            hiNachosCustomLabelsResponseAttributesList.add(hiNachosCustomLabelsResponseAttributes);
        }
        hiNachosCustomLabelsResponse.setMessage("Successfully fetched custom Label details");
        hiNachosCustomLabelsResponse.setHiNachosCustomLabelsResponseAttributesList(hiNachosCustomLabelsResponseAttributesList);
        return hiNachosCustomLabelsResponse;
    }

    public List<Map<String, Map<String, String>>> parseCustomLabelsJsonToMap(String jsonString) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        List<Map<String, Map<String, String>>> formattedMapList = new ArrayList<>();
         Map<String, Map<String, String>> formattedMap = new HashMap<>();
         formattedMap = gson.fromJson(jsonString, mapType);
         formattedMapList.add(formattedMap);
        return formattedMapList;
    }

    /**
     * Inner class representing reward counts.
     */
    @Component
    @Getter
    @Setter
    public static class RewardCounts {
        private long totalCount;
        private long redeemedYCount;
        private long redeemedNCount;
    }
}

