package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.BlockedUserService;
import boerenkool.communication.dto.BlockedUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void blockUser(@RequestParam int userToBlockId, @RequestParam int userBlockingId) {
        blockedUserService.blockUser(userToBlockId, userBlockingId);
    }

    @PostMapping("/unblock")
    public void unblockUser(@RequestParam int userToUnblockId, @RequestParam int userBlockingId) {
        blockedUserService.unblockUser(userToUnblockId, userBlockingId);
    }

    @GetMapping("/{userId}")
    public List<BlockedUserDTO> getBlockedUsers(@PathVariable int userId) {
        List<User> blockedUsers = blockedUserService.getBlockedUsers(userId);

        // Convert each User to BlockedUserDTO to exclude sensitive information
        return blockedUsers.stream()
                .map(user -> new BlockedUserDTO(user.getUsername()))
                .collect(Collectors.toList());
    }

    @GetMapping("/is-blocked")
    public boolean isUserBlocked(@RequestParam int userToCheckId, @RequestParam int userCheckingId) {
        return blockedUserService.isUserBlocked(userToCheckId, userCheckingId);
    }
}
