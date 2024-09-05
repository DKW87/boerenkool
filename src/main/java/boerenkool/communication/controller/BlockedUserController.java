package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.BlockedUserService;
import boerenkool.communication.dto.BlockedUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blocked-users")
public class BlockedUserController {

    private final BlockedUserService blockedUserService;

    @Autowired
    public BlockedUserController(BlockedUserService blockedUserService) {
        this.blockedUserService = blockedUserService;
    }

    @PostMapping("/block")
    public ResponseEntity<String> blockUser(@RequestParam int userToBlockId, @RequestParam int userBlockingId) {
        try {
            blockedUserService.blockUser(userToBlockId, userBlockingId);
            return new ResponseEntity<>("User blocked successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to block user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/unblock")
    public ResponseEntity<String> unblockUser(@RequestParam int userToUnblockId, @RequestParam int userBlockingId) {
        try {
            blockedUserService.unblockUser(userToUnblockId, userBlockingId);
            return new ResponseEntity<>("User unblocked successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to unblock user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<BlockedUserDTO>> getBlockedUsers(@PathVariable int userId) {
        try {
            List<User> blockedUsers = blockedUserService.getBlockedUsers(userId);

            // Log de geblokkeerde gebruikers om te controleren of de juiste IDs worden opgehaald
            blockedUsers.forEach(user -> System.out.println("Geblokkeerde gebruiker ID: " + user.getUserId()));

            List<BlockedUserDTO> blockedUserDTOs = blockedUsers.stream()
                    .map(user -> new BlockedUserDTO(user.getUserId(), user.getUsername()))  // Zorg ervoor dat userId correct wordt gezet
                    .collect(Collectors.toList());

            return new ResponseEntity<>(blockedUserDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/is-blocked")
    public ResponseEntity<Boolean> isUserBlocked(@RequestParam int userToCheckId, @RequestParam int userCheckingId) {
        try {
            boolean isBlocked = blockedUserService.isUserBlocked(userToCheckId, userCheckingId);
            return new ResponseEntity<>(isBlocked, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
