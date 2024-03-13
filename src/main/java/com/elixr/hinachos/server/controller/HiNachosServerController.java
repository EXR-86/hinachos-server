package com.elixr.hinachos.server.controller;

import com.elixr.hinachos.server.dto.HiNachosRewardsSummaryDataItem;
import com.elixr.hinachos.server.request.*;
import com.elixr.hinachos.server.dto.HiNachosRewardsDataWrapper;
import com.elixr.hinachos.server.response.*;
import com.elixr.hinachos.server.service.HiNachosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hinachos") // Set the base path for all APIs in this controller
@Slf4j
public class HiNachosServerController {

    private final HiNachosService hiNachosService;

    public HiNachosServerController(HiNachosService hiNachosService) {
        this.hiNachosService = hiNachosService;
    }

    @GetMapping("/hinachos-server")
    public ResponseEntity sendMessage() {
        HiNachosServerBaseResponse heyNachosServerSuccessResponse = new HiNachosServerBaseResponse();
        heyNachosServerSuccessResponse.setMessage("Hello From server");
        return new ResponseEntity(heyNachosServerSuccessResponse, HttpStatus.OK);
    }

    @PostMapping("/assignRewards")
    public ResponseEntity assignRewardsToUser(@RequestBody HiNachosRewardAssignmentRequest hiNachosRewardAssignmentRequest) {
        HiNachosServerBaseResponse hiNachosServerBaseResponse = new HiNachosServerBaseResponse();
        log.info("Received message to process:- " + hiNachosRewardAssignmentRequest.getRewardDetails().getRecognitionMessage());
        try {
            hiNachosServerBaseResponse = hiNachosService.assignRewardsToUser(hiNachosRewardAssignmentRequest);
            return hiNachosServerBaseResponse.isSuccess() ? new ResponseEntity(hiNachosServerBaseResponse, HttpStatus.OK)
                    : new ResponseEntity(hiNachosServerBaseResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            String errorMessage = "There is an server error occurred while assigning rewards. Unable to proceed.";
            log.error("An error occurred while assigning rewards, Reason:- ", exception);
            hiNachosServerBaseResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            hiNachosServerBaseResponse.setSuccess(false);
            hiNachosServerBaseResponse.setMessage(errorMessage);
            return  new ResponseEntity(hiNachosServerBaseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getRewardDetails")
    public ResponseEntity getRewardDetails(@RequestBody HiNachosRewardsSearchCriteria hiNachosRewardsSearchCriteria) {
        HiNachosRewardsResponse hiNachosRewardsResponse = HiNachosRewardsResponse.builder().build();
        try {
            HiNachosRewardsDataWrapper hinachosRewardsDataWrapper = hiNachosService.getRewardDetails(hiNachosRewardsSearchCriteria);
            hiNachosRewardsResponse.setHinachosRewardsDataWrapper(hinachosRewardsDataWrapper);
            hiNachosRewardsResponse.setMessage("Successfully fetched reward details for user:- " + hiNachosRewardsSearchCriteria.getUserId());
            hiNachosRewardsResponse.setSuccess(true);
            return new ResponseEntity(hiNachosRewardsResponse, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("An error occurred while fetching rewards detail for user:- " + hiNachosRewardsSearchCriteria.getUserId(), exception);
            hiNachosRewardsResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            hiNachosRewardsResponse.setSuccess(false);
            hiNachosRewardsResponse.setMessage("There is an server error occurred while fetching reward details. Unable to proceed.");
            return new ResponseEntity(hiNachosRewardsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/leaderboard/top-users")
    public ResponseEntity getTopUsers(@RequestBody HiNachosRewardsSummarySearchCriteria hiNachosRewardsSummarySearchCriteria) {
        HiNachosLeaderBoardResponse hiNachosLeaderBoardResponse = new HiNachosLeaderBoardResponse();
        try {
            List<HiNachosRewardsSummaryDataItem> hiNachosRewardsDataItemList = hiNachosService.getTopUsers(hiNachosRewardsSummarySearchCriteria);
            hiNachosLeaderBoardResponse.setHiNachosRewardsDataItemList(hiNachosRewardsDataItemList);
            hiNachosLeaderBoardResponse.setStatusCode(HttpStatus.OK.value());
            hiNachosLeaderBoardResponse.setSuccess(true);
            hiNachosLeaderBoardResponse.setMessage("Successfully fetched top users for Leader board");
            return new ResponseEntity(hiNachosLeaderBoardResponse, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("An error occurred while fetching details for users gainers", exception);
            hiNachosLeaderBoardResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            hiNachosLeaderBoardResponse.setSuccess(false);
            hiNachosLeaderBoardResponse.setMessage("There is an server error occurred while fetching reward details. Unable to proceed.");
            return new ResponseEntity(hiNachosLeaderBoardResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/tags")
    public ResponseEntity getTagDetails(@RequestBody HiNachosTagsSearchCriteria hiNachosTagsSearchCriteria) {
        HiNachosTagsResponse hiNachosTagsResponse = new HiNachosTagsResponse();
        try {
            hiNachosTagsResponse = hiNachosService.getTagDetails(hiNachosTagsSearchCriteria);
            hiNachosTagsResponse.setStatusCode(HttpStatus.OK.value());
            hiNachosTagsResponse.setSuccess(true);
            return new ResponseEntity(hiNachosTagsResponse, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("An error occurred while fetching details for tags", exception);
            hiNachosTagsResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            hiNachosTagsResponse.setSuccess(false);
            hiNachosTagsResponse.setMessage("There is an server error occurred while fetching tag details. Unable to proceed.");
            return new ResponseEntity(hiNachosTagsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/customize/labels")
    public ResponseEntity updateCustomLabelAttributes(@RequestBody HiNachosCustomNameRegistryRequest hiNachosCustomNameRegistryRequest) {
        HiNachosServerBaseResponse hiNachosServerBaseResponse = new HiNachosServerBaseResponse();
        try {
            hiNachosServerBaseResponse = hiNachosService.updateCustomLabels(hiNachosCustomNameRegistryRequest);
            hiNachosServerBaseResponse.setStatusCode(HttpStatus.OK.value());
            hiNachosServerBaseResponse.setSuccess(true);
            return new ResponseEntity(hiNachosServerBaseResponse, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("An error occurred while saving custom Label attributes", exception);
            hiNachosServerBaseResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            hiNachosServerBaseResponse.setSuccess(false);
            hiNachosServerBaseResponse.setMessage("There is an server error occurred while saving custom Label attributes. Unable to proceed.");
            return new ResponseEntity(hiNachosServerBaseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getCustomLabels")
    public ResponseEntity getCustomLabels() {
        HiNachosCustomLabelsResponse hiNachosCustomLabelsResponse = new HiNachosCustomLabelsResponse();
        try {
            hiNachosCustomLabelsResponse = hiNachosService.getAllCustomLabels();
            hiNachosCustomLabelsResponse.setStatusCode(HttpStatus.OK.value());
            hiNachosCustomLabelsResponse.setSuccess(true);
            return new ResponseEntity(hiNachosCustomLabelsResponse, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("An error occurred while fetching custom Label details", exception);
            hiNachosCustomLabelsResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            hiNachosCustomLabelsResponse.setSuccess(false);
            hiNachosCustomLabelsResponse.setMessage("There is an server error occurred while fetching custom Label details. Unable to proceed.");
            return new ResponseEntity(hiNachosCustomLabelsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

