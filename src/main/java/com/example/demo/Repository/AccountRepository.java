package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.example.demo.Models.Account;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends  JpaRepository<Account, Long> {

/*@Query(value = """
     Select a.*
       from Account a
       where a.last_update =
       (
           select max(b.last_update)
           from account b
           where a.account_number = b.account_number
       )
       order by a.id
""", nativeQuery = true)
    List<Account> findAllAccountOrderByRecentlyDate();
*/

}