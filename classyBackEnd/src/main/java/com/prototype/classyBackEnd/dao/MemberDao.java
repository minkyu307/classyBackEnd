package com.prototype.classyBackEnd.dao;

import com.prototype.classyBackEnd.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberDao {

    private final EntityManager em;

    public Optional<Member> save(Member member){
        em.persist(member);
        return Optional.ofNullable(member);
    }

    public Optional<Member> findOneByKakaoId(Long id) throws SQLException {
        try {
            return Optional.ofNullable(em.createQuery("select m from Member m where m.kakaoId=?1",Member.class).setParameter(1,id).getSingleResult());
        }
        catch (Exception e){
            throw new SQLException("KakaoAccountNotFound");
        }

    }

    public Optional<Member> findOneByClassyNickName(String classyNickName) throws SQLException{
        try {
            return Optional.ofNullable(em.createQuery("select m from Member m where m.classyNickName=?1",Member.class)
                    .setParameter(1,classyNickName).getSingleResult());
        }
        catch (Exception e){
            throw new SQLException("AccountNotFound");
        }
    }

    public Optional<Member> findOneByEmail(String email) throws SQLException {
        try {
            return Optional.ofNullable(em.createQuery("select m from Member m where m.email=?1",Member.class).setParameter(1,email).getSingleResult());
        }
        catch (Exception e){
            throw new SQLException("NoSuchEmail");
        }

    }

    public List<Member> findMembersByKakaoId(Long id){
        return em.createQuery("select m from Member m where m.kakaoId=?1")
                .setParameter(1,id)
                .getResultList();
    }

    public List<Member> findMembersByEmail(String email){
        return em.createQuery("select m from Member m where m.email=?1")
                .setParameter(1,email)
                .getResultList();
    }

    public List<Member> findMembersByClassyNickName(String nickName){
        return em.createQuery("select m from Member m where m.classyNickName=?1")
                .setParameter(1,nickName)
                .getResultList();
    }

    public void mailAuthUpdate(String email, String key){
        em.createQuery("update Member m set m.authKey=?1 where m.email=?2").setParameter(1,key).setParameter(2,email).executeUpdate();
    }

    public void authStatusUpdate(String email){
        em.createQuery("update Member m set m.authStatus=1 where m.email=?1").setParameter(1,email).executeUpdate();
    }

    public boolean emailAuthCorrect(String email, String key){
        List<Member> members = em.createQuery("select m from Member m where m.email=?1 and m.authKey=?2").setParameter(1,email).setParameter(2,key).getResultList();
        if (members.isEmpty())
            return false;
        return true;
    }

    public void deleteMemberByEmail(Member member){
        em.createQuery("delete from Member m where m.email=?1")
                .setParameter(1,member.getEmail()).executeUpdate();
    }

    public void persistAndClear(){
        em.flush();
        em.clear();
    }
}
