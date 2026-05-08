package com.promptcraft.promptcraft.controller;

import com.promptcraft.promptcraft.dto.participant.InviteParticipantRequest;
import com.promptcraft.promptcraft.dto.participant.ParticipantResponse;
import com.promptcraft.promptcraft.dto.participant.UpdateParticipantRole;
import com.promptcraft.promptcraft.service.ParticipantService;
import jakarta.validation.Valid;
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
        return ResponseEntity.ok(participantService.getAllMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<ParticipantResponse> inviteMember(
            @PathVariable Long projectId,
            @RequestBody @Valid InviteParticipantRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(participantService.inviteParticipant(projectId, request));
    }

    @PatchMapping("/{participantId}")
    public ResponseEntity<ParticipantResponse> updatePartipcpantRole(
            @PathVariable Long projectId,
            @PathVariable Long particpantId,
            @RequestBody @Valid UpdateParticipantRole request
    ){
        return ResponseEntity.ok(participantService.updateParticipantRole(projectId, particpantId, request));
    }

    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long projectId,
            @PathVariable Long particpantId
    ){
        participantService.removeParticipant(projectId, particpantId);
        return ResponseEntity.noContent().build();
    }


}
