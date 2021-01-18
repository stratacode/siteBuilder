create function find_users_for_site(site_id bigint, from_time timestamp, to_time timestamp, start_ix integer, num_to_fetch integer)
   returns setof user_profile as $$
      select user_profile.* from user_profile, user_session 
         where user_profile.id = user_session.user_ and user_session.site = site_id and 
                user_session.last_event_time > from_time and user_session.last_event_time < to_time
         group by user_profile.id 
         order by max(user_session.last_event_time) desc
         limit num_to_fetch 
         offset start_ix 
   $$ language sql stable;

create function find_reg_users_for_site(site_id bigint, from_time timestamp, to_time timestamp, start_ix integer, num_to_fetch integer)
   returns setof user_profile as $$
      select user_profile.* from user_profile, user_session
         where user_profile.id = user_session.user_ and user_session.site = site_id and user_profile.user_name is not null and
                user_session.last_event_time > from_time and user_session.last_event_time < to_time
         group by user_profile.id
         order by max(user_session.last_event_time) desc
         limit num_to_fetch
         offset start_ix
   $$ language sql stable;


create function count_users_for_site(site_id bigint, from_time timestamp, to_time timestamp)
   returns bigint as $$
      select count(*) from user_profile where exists (select 1 from user_session where user_session.user_ = user_profile.id and user_session.site = site_id and user_session.last_event_time > from_time and user_session.last_event_time < to_time)
   $$ language sql stable;

