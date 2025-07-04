package Repository;

import Entity.Member;

public class MemberDao extends HibernateDao<Member, Long> {

    public MemberDao(){
        super(Member.class);
    }

}
