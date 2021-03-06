package com.lzh0108.community;

import com.lzh0108.community.dao.*;
import com.lzh0108.community.entity.DiscussPost;
import com.lzh0108.community.entity.LoginTicket;
import com.lzh0108.community.entity.Message;
import com.lzh0108.community.entity.User;
import com.lzh0108.community.entity.query.result.ReplyPostResult;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private CommentMapper commentMapper;


    @Test
    public void testSelectUser() {
        User user = userMapper.selectByUserName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        user.setEmail("test@qq.com");
        user.setSalt("qwe");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());

    }

    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "test123");
        System.out.println(rows);
    }


    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10, 0);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("aba");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);

    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("aba");

        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("aba", 1);

        loginTicket = loginTicketMapper.selectByTicket("aba");

        System.out.println(loginTicket);

    }

    @Test
    public void testSelectLetters() {
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int rows = messageMapper.selectConversationCount(111);
        System.out.println(rows);

        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }

        rows = messageMapper.selectLetterCount("111_112");
        System.out.println(rows);

        rows = messageMapper.selectLetterUnreadCount(111, null);
        System.out.println(rows);

        rows = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(rows);
    }

    @Test
    public void testSelectReplyPost() {
        List<ReplyPostResult> list = discussPostMapper.selectReplyDiscussPosts(155, 0, 1);
        for (ReplyPostResult post : list) {
            System.out.println(post.toString());
        }
    }

    @Test
    public void testUpdateCommentStatusByPostId(){
        commentMapper.updateStatusByPostId(285,1);
    }
}
