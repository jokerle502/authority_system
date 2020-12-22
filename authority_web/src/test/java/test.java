import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hopu.domain.UserRole;
import com.hopu.service.IUserRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jws.soap.SOAPBinding;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class test {

    @Autowired
    private IUserRoleService userRoleService;

    @Test
    public void test(){
        List<UserRole> list = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", "9dde96c997894968bb1ebbbc90aa1ccc"));
    }
}
