package com.prototype.classyBackEnd.dao;

import com.prototype.classyBackEnd.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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

    public Optional<Member> findOneByKakaoId(Long id){
        return Optional.ofNullable(em.createQuery("select m from Member m where m.kakaoId=?1",Member.class).setParameter(1,id).getSingleResult());
    }

    public List<Member> findMembersByKakaoId(Member member){
        return em.createQuery("select m from Member m where m.kakaoId=?1")
                .setParameter(1,member.getKakaoId())
                .getResultList();
    }

    public List<Member> findMembersByEmail(Member member){
        return em.createQuery("select m from Member m where m.email=?1")
                .setParameter(1,member.getEmail())
                .getResultList();
    }

    public List<Member> findMembersByClassyNickName(Member member){
        return em.createQuery("select m from Member m where m.classyNickName=?1")
                .setParameter(1,member.getClassyNickName())
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
}
