package com.elixr.hinachos.server.dao;

import com.elixr.hinachos.server.constants.HiNachosQueryConstants;
import com.elixr.hinachos.server.constants.HiNachosServerConstants;
import com.elixr.hinachos.server.dto.HiNachosRewardsSummaryDataItem;
import com.elixr.hinachos.server.persistence.domain.*;
import com.elixr.hinachos.server.persistence.repository.*;
import com.elixr.hinachos.server.request.HiNachosRewardsSummarySearchCriteria;
import com.elixr.hinachos.server.request.HiNachosTagsSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Component
public class HiNachosDao {

    private final HiNachosRewardsRepository hiNachosRewardsRepository;

    private final HiNachosUserRepository hiNachosUserRepository;

    private final HiNachosChannelRepository hiNachosChannelRepository;

    private final HiNachosTagsRepository hiNachosTagsRepository;

    private final HiNachosCustomLabelsRepository hiNachosCustomLabelsRepository;
    private EntityManager entityManager;
    public HiNachosDao(HiNachosRewardsRepository hiNachosRewardsRepository, HiNachosUserRepository hiNachosUserRepository, HiNachosChannelRepository hiNachosChannelRepository, HiNachosTagsRepository hiNachosTagsRepository, HiNachosCustomLabelsRepository hiNachosCustomLabelsRepository, EntityManager entityManager) {
        this.hiNachosRewardsRepository = hiNachosRewardsRepository;
        this.hiNachosUserRepository = hiNachosUserRepository;
        this.hiNachosChannelRepository = hiNachosChannelRepository;
        this.hiNachosTagsRepository = hiNachosTagsRepository;
        this.hiNachosCustomLabelsRepository = hiNachosCustomLabelsRepository;
        this.entityManager = entityManager;
    }

    public void saveRewardDetails(HiNachosRewardsDataEntity hiNachosRewardsDataEntity) {
        if(ObjectUtils.isEmpty(hiNachosRewardsDataEntity.getId())) {
            String uuidValue= UUID.randomUUID().toString();
            hiNachosRewardsDataEntity.setId(uuidValue);
        }
        hiNachosRewardsRepository.save(hiNachosRewardsDataEntity);

    }
    public void saveUserDetails(HiNachosUserDataEntity hiNachosUserDataEntity) {
        if(ObjectUtils.isEmpty(hiNachosUserDataEntity.getId())) {
            String uuidValue= UUID.randomUUID().toString();
            hiNachosUserDataEntity.setId(uuidValue);
            hiNachosUserDataEntity.setCreatedDate(new Date());
        }
        hiNachosUserDataEntity.setModifiedDate(new Date());
        hiNachosUserRepository.save(hiNachosUserDataEntity);

    }

    public void saveChannelDetails(HiNachosChannelDataEntity hiNachosChannelDataEntity) {
        if(ObjectUtils.isEmpty(hiNachosChannelDataEntity.getId())) {
            String uuidValue= UUID.randomUUID().toString();
            hiNachosChannelDataEntity.setId(uuidValue);
        }
        hiNachosChannelRepository.save(hiNachosChannelDataEntity);
    }

    public void saveCustomLabelDetails(HiNachosCustomLabelsDataEntity hiNachosCustomLabelsDataEntity) {
        if(ObjectUtils.isEmpty(hiNachosCustomLabelsDataEntity.getId())) {
            String uuidValue= UUID.randomUUID().toString();
            hiNachosCustomLabelsDataEntity.setId(uuidValue);
        }
        hiNachosCustomLabelsRepository.save(hiNachosCustomLabelsDataEntity);
    }

    public HiNachosCustomLabelsDataEntity getCustomLabelByDomain(String domainName) {
        return hiNachosCustomLabelsRepository.findByDomainName(domainName);
    }

    public List<HiNachosCustomLabelsDataEntity> getAllCustomLabels() {
        return hiNachosCustomLabelsRepository.findAll();
    }

    public HiNachosUserDataEntity getUserByUserId(String userId) {
        return hiNachosUserRepository.findByExternalId(userId);
    }

    public void saveHashtags(HiNachosHashTagDataEntity hiNachosHashTagDataEntityList) {
        hiNachosTagsRepository.save(hiNachosHashTagDataEntityList);
    }

    public HiNachosHashTagDataEntity getTagById(String hashTagId) {
        return hiNachosTagsRepository.findByExternalId(hashTagId);
    }

    public HiNachosChannelDataEntity getChannelByChannelId(String channelId) { return hiNachosChannelRepository.findByExternalId(channelId);}

    public List<HiNachosRewardsSummaryDataItem> getTopUsers(HiNachosRewardsSummarySearchCriteria hiNachosRewardsSummarySearchCriteria) {
        String jpaQueryForRewardsSummary = HiNachosQueryConstants.GET_REWARDS_SUMMARY_SENDERS_AND_RECEIVERS_QUERY_JPQL;
        StringBuilder whereClauseText = new StringBuilder();
        if(!ObjectUtils.isEmpty(hiNachosRewardsSummarySearchCriteria.getUserType())) {
            if(HiNachosServerConstants.USER_TYPE_SENDER.equals(hiNachosRewardsSummarySearchCriteria.getUserType())) {
                jpaQueryForRewardsSummary = HiNachosQueryConstants.GET_REWARDS_SUMMARY_SENDERS_QUERY_JPQL;

            } else if(HiNachosServerConstants.USER_TYPE_RECEIVER.equals(hiNachosRewardsSummarySearchCriteria.getUserType())) {
                jpaQueryForRewardsSummary = HiNachosQueryConstants.GET_REWARDS_SUMMARY_RECEIVERS_QUERY_JPQL;
            }
        }
        if(!ObjectUtils.isEmpty(hiNachosRewardsSummarySearchCriteria.getChannelId())) {
            addWhereOrAndClauseToQuery(whereClauseText);
            whereClauseText.append(" r.channelId = :"+HiNachosQueryConstants.CHANNELID + " ");
        }
        if(!ObjectUtils.isEmpty(hiNachosRewardsSummarySearchCriteria.getStartDate()) && !ObjectUtils.isEmpty(hiNachosRewardsSummarySearchCriteria.getEndDate())) {
            addWhereOrAndClauseToQuery(whereClauseText);
            whereClauseText.append(" r.timeOfRecognition BETWEEN :" + HiNachosQueryConstants.STARTDATE + " AND :"+HiNachosQueryConstants.ENDDATE + " ");
        }

        jpaQueryForRewardsSummary = jpaQueryForRewardsSummary.replace(HiNachosQueryConstants.WHERE_CLAUSE_PLACE_HOLDER, whereClauseText);
        TypedQuery<HiNachosRewardsSummaryDataItem> rewardsSummaryDataItemTypedQuery = entityManager.createQuery(jpaQueryForRewardsSummary, HiNachosRewardsSummaryDataItem.class);
        if (jpaQueryForRewardsSummary.contains(HiNachosQueryConstants.CHANNELID)) {
            rewardsSummaryDataItemTypedQuery.setParameter(HiNachosQueryConstants.CHANNELID, hiNachosRewardsSummarySearchCriteria.getChannelId());

        }
        if (jpaQueryForRewardsSummary.contains(HiNachosQueryConstants.STARTDATE) && jpaQueryForRewardsSummary.contains(HiNachosQueryConstants.ENDDATE)) {
            rewardsSummaryDataItemTypedQuery.setParameter(HiNachosQueryConstants.STARTDATE, hiNachosRewardsSummarySearchCriteria.getStartDate());
            rewardsSummaryDataItemTypedQuery.setParameter(HiNachosQueryConstants.ENDDATE, hiNachosRewardsSummarySearchCriteria.getEndDate());
        }
        if (hiNachosRewardsSummarySearchCriteria.getPageNumber() != null) {
            rewardsSummaryDataItemTypedQuery.setFirstResult(Math.toIntExact(hiNachosRewardsSummarySearchCriteria.getPageNumber()));
            rewardsSummaryDataItemTypedQuery.setMaxResults(Math.toIntExact(HiNachosServerConstants.DISPLAY_TABLE_PAGE_SIZE));
        }

        List<HiNachosRewardsSummaryDataItem> hiNachosRewardsSummaryDataItemList = rewardsSummaryDataItemTypedQuery.getResultList();
        return hiNachosRewardsSummaryDataItemList;
    }

    private void addWhereOrAndClauseToQuery(StringBuilder whereClauseText) {
        if(!whereClauseText.toString().contains(HiNachosQueryConstants.WHERE_KEYWORD)) {
            whereClauseText.append(" " + HiNachosQueryConstants.WHERE_KEYWORD + " ");
        } else {
            whereClauseText.append(" " + HiNachosQueryConstants.AND_KEYWORD + " ");
        }
    }

    public List<HiNachosRewardsSummaryDataItem> getTagDetails(HiNachosTagsSearchCriteria hiNachosTagsSearchCriteria) {
        String jpaQueryForTagSummary = HiNachosQueryConstants.GET_REWARDS_SUMMARY_BY_HASHTAG_RECEIVERS_QUERY_JPQL;
        StringBuilder whereClauseText = new StringBuilder();
        if(!ObjectUtils.isEmpty(hiNachosTagsSearchCriteria.getTagId())) {
            addWhereOrAndClauseToQuery(whereClauseText);
            whereClauseText.append(" r.hashTagId = :"+HiNachosQueryConstants.TAG_ID + " ");
        }
        if(!ObjectUtils.isEmpty(hiNachosTagsSearchCriteria.getStartDate()) && !ObjectUtils.isEmpty(hiNachosTagsSearchCriteria.getEndDate())) {
            addWhereOrAndClauseToQuery(whereClauseText);
            whereClauseText.append("r.hashTagId IS NOT NULL AND r.timeOfRecognition BETWEEN :" + HiNachosQueryConstants.STARTDATE + " AND :"+HiNachosQueryConstants.ENDDATE + " ");
        }
        jpaQueryForTagSummary = jpaQueryForTagSummary.replace(HiNachosQueryConstants.WHERE_CLAUSE_PLACE_HOLDER, whereClauseText);

        TypedQuery<HiNachosRewardsSummaryDataItem> tagsSummaryDataItemTypedQuery = entityManager
                .createQuery(jpaQueryForTagSummary, HiNachosRewardsSummaryDataItem.class);
        if (jpaQueryForTagSummary.contains(HiNachosQueryConstants.STARTDATE) && jpaQueryForTagSummary.contains(HiNachosQueryConstants.ENDDATE)) {
            tagsSummaryDataItemTypedQuery.setParameter(HiNachosQueryConstants.STARTDATE, hiNachosTagsSearchCriteria.getStartDate());
            tagsSummaryDataItemTypedQuery.setParameter(HiNachosQueryConstants.ENDDATE, hiNachosTagsSearchCriteria.getEndDate());
        }
        if (jpaQueryForTagSummary.contains(HiNachosQueryConstants.CONDITION_TO_GET_TAG_DETAILS_BY_TAG_ID)) {
            tagsSummaryDataItemTypedQuery.setParameter(HiNachosQueryConstants.TAG_ID, hiNachosTagsSearchCriteria.getTagId());
        }
        if (hiNachosTagsSearchCriteria.getPageNumber() != null) {
            tagsSummaryDataItemTypedQuery.setFirstResult(Math.toIntExact(hiNachosTagsSearchCriteria.getPageNumber()));
            tagsSummaryDataItemTypedQuery.setMaxResults(Math.toIntExact(HiNachosServerConstants.DISPLAY_TABLE_PAGE_SIZE));
        }
        List<HiNachosRewardsSummaryDataItem> hiNachosTagsSummaryDataItemList = tagsSummaryDataItemTypedQuery.getResultList();
        return hiNachosTagsSummaryDataItemList;
    }
}
