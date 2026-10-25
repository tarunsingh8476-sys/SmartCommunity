package com.smartcommunity.dashboard;
import com.smartcommunity.user.*;import com.smartcommunity.server.*;import com.smartcommunity.membership.*;import com.smartcommunity.conversation.*;import com.smartcommunity.notification.*;
import org.springframework.stereotype.Service;import java.util.*;
@Service
public class DashboardService {
 private final UserService users;
 private final ServerMemberRepository memberships;
 private final ConversationParticipantRepository conversations;
 private final NotificationService notifications;
 private final ServerService servers;

 public DashboardService(UserService u, ServerMemberRepository m, ConversationParticipantRepository c, NotificationService n, ServerService s) {
  users = u;
  memberships = m;
  conversations = c;
  notifications = n;
  servers = s;
 }

 public Map<String, Object> get(String email) {
  var profile = users.publicProfile(users.byEmail(email));
  var serverList = servers.list(email);
  var conversationCount =
          conversations.findByUserEmailIgnoreCase(email).size();
  var unreadNotificationCount = notifications.unread(email);

  var recentNotifications = notifications.list(email)
          .stream()
          .limit(5)
          .toList();

  Map<String, Object> summary = new LinkedHashMap<>();
  summary.put("joinedCommunities", serverList.size());
  summary.put("activeConversations", conversationCount);
  summary.put("unreadNotifications", unreadNotificationCount);

  Map<String, Object> response = new LinkedHashMap<>();
  response.put("profile", profile);
  response.put("servers", serverList);
  response.put("conversationCount", conversationCount);
  response.put("unreadNotificationCount", unreadNotificationCount);
  response.put("summary", summary);
  response.put("recentNotifications", recentNotifications);

  return response;
 }
}
