package com.promptcraft.promptcraft.controller;

import com.promptcraft.promptcraft.dto.participant.InviteParticipantRequest;
import com.promptcraft.promptcraft.dto.participant.ParticipantResponse;
import com.promptcraft.promptcraft.dto.participant.UpdateParticipantRole;
import com.promptcraft.promptcraft.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/members")
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping
    public ResponseEntity<List<ParticipantResponse>> getAllMembers(@PathVariable Long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(participantService.getAllMembers(projectId, userId));
    }

    @PostMapping
    public ResponseEntity<List<ParticipantResponse>> inviteMember(
            @PathVariable Long projectId,
            @RequestBody InviteParticipantRequest request
            ){
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(participantService.inviteParticipant(projectId, request, userId));
    }

    @PatchMapping("/{participantId")
    public ResponseEntity<ParticipantResponse> updatePartipcpantRole(
            @PathVariable Long projectId,
            @PathVariable Long particpantId,
            @RequestBody UpdateParticipantRole request
    ){
        Long userId = 1L;
        return ResponseEntity.ok(participantService.updateParticipantRole(projectId, particpantId, request, userId));
    }

    @DeleteMapping("/{participantId")
    public ResponseEntity<ParticipantResponse> updatePartipcpantRole(
            @PathVariable Long projectId,
            @PathVariable Long particpantId
    ){
        Long userId = 1L;
        return ResponseEntity.ok(participantService.deleteParticipant(projectId, particpantId, userId));
    }


}
