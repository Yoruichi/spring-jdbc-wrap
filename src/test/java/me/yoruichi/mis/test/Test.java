package me.yoruichi.mis.test;

import com.google.common.collect.Lists;
import me.yoruichi.mis.dao.FooDao;
import me.yoruichi.mis.po.Foo;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by yoruichi on 16/10/26.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class Test {

    @Autowired
    private FooDao fooDao;

    @Rollback
    @org.junit.Test
    public void test() {
        Foo foo = new Foo();
        fooDao.getTemplate().update("delete from foo");
        foo.setName("testA");
        try {
            fooDao.insertOne(foo);
            fooDao.insertOne(foo);
            int id = fooDao.insertOneGetId(foo);
            Assert.assertEquals(3, fooDao.selectMany(foo).size());
            foo.setId(id);
            foo.setAge(27);
            foo.setName("testB");
            foo.setGender(false);
            fooDao.insertOrUpdate(foo);
            Foo f = new Foo();
            f.in("name", new String[] {"testA", "testB"});
            Assert.assertEquals(3, fooDao.selectMany(f).size());
            Foo f1 = new Foo();
            f1.gt("age", 22);
            Assert.assertEquals("testB", fooDao.select(f1).getName());
            Foo f2 = new Foo();
            f2.in("age", new Integer[] {22, 27});
            Assert.assertEquals(3, fooDao.selectMany(f).size());
            Assert.assertEquals(3, fooDao.selectMany(f.or(f1).or(f2)).size());
            f1.update("email", "whatever@google.com");
            fooDao.updateOne(f1);
            f.update("gender", true);
            fooDao.updateMany(Lists.newArrayList(f,f2));
            foo = new Foo();
            foo.setGender(true).orderBy("id").setAsc();
            List<Foo> fl = fooDao.selectMany(foo);
            fl.stream().forEach(foo1 -> foo1.setGender(false));
            System.out.println(fl.get(0).getEmail());
            fooDao.insertOrUpdateMany(fl);
            foo.setGender(false);
            Assert.assertEquals(3, fooDao.selectMany(foo).size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}