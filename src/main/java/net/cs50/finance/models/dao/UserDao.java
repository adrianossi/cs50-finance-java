package net.cs50.finance.models.dao;

import net.cs50.finance.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by cbay on 5/10/15.
 */

// DAOs are the mechanism by which the objs are actually put into the db.
// Hibernate and Spring make this work for us.
// @trans =
// @repo = this makes this class a DAO interface, along with the fact that
//   it extends a crudRepo (create, read, update, delete)
@Transactional
@Repository
public interface UserDao extends CrudRepository<User, Integer> {

    User findByUserName(String userName);

    User findByUid(int uid);

}
