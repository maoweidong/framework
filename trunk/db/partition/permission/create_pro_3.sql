


-----------------------------------------------
-- Export file for user SYS                  --
-- Created by user on 2008-4-23, ���� 05:08:59 --
-----------------------------------------------

spool start1111.log

prompt
prompt Creating procedure AD_CLIENT_TSP_CREATE
prompt =======================================
prompt
create or replace procedure Ad_Client_Tsp_Create(p_ad_client_id in number,
                                                 p_size in varchar2,
                                                 p_dirpath in varchar2,
                                                 p_username in varchar2) as
    /*2008.03.28 zjf edit
      p_dirpath ��Ӧ��tablespace�ı���·��
      p_ad_client_id �ǹ�˾��Ӧ��ad_client_id��ֵ
      p_size ��Ӧ���Ƕ���Tablespace size�Ĵ�С
      p_username ��Ӧ����oracle���û��� �磺nds4,bidnds4
    */
    v_tablespace_path    varchar2(200); --tablespace������·��
    v_ad_client_id       number(10); --��˾��Ӧ��id��ֵ
    v_addPart_table_name varchar2(100); --tablespace�������ļ�������
    v_part_table         varchar2(100); --����ad_client_id����һ������
    v_size               varchar2(20); --tablespace�Ĵ�С
    v_table_name         varchar2(100); --��Ҫ���ӷ����ı���
begin
    --�������������Ч�Եļ��
    if p_ad_client_id is null then
        raise_application_error(-20001, 'δ���幫˾����Ӧ��ad_client_id!');
    end if;
    if p_username is null then
        raise_application_error(-20001, 'δ��дϵͳ�û�������!');
    end if;
    if p_size is null then
        raise_application_error(-20001, 'δ�������ݿ��ļ��Ĵ�С!');
    end if;
    if p_dirpath is null then
        raise_application_error(-20001, 'δ���������ļ�������ļ���λ��!');
    end if;

    v_ad_client_id := p_ad_client_id;
    --��ȡ�������еķ���
    v_addPart_table_name := 'g_2000q' || to_char(v_ad_client_id);
    --���� Tablespace������·��
    v_tablespace_path := p_dirpath || '/g_2000q' || to_char(v_ad_client_id) ||
                         '.dbf';
    /*����Tablespace�Ĵ�С
    ���Ϊ�գ������ó�Ĭ��100M�Ĵ�С
    �����Ϊ�գ������óɹ�����Ա���õĴ�С*/
    if p_size is null then
        v_size := '100M';
    else
        v_size := p_size;
    end if;

    --���� Tablespace
    /*  
     dbms_output.put_line('create tablespace ' || v_addPart_table_name ||
                          ' datafile ' || '''' || v_tablespace_path || '''' ||
                          ' size ' || v_size);
    */
    execute immediate 'create tablespace ' || v_addPart_table_name ||
                      ' datafile ' || '''' || v_tablespace_path || '''' ||
                      ' size ' || v_size;
    --||' default storage (initial 100k next 100k minextents 1 maxextents unlimited pctincrease 1)';
    --����������ķ���
    v_part_table := 'P' || to_char(v_ad_client_id);

    /*��Ҫ������һ��ѭ�� ѭ���ı�����ֵ���Ǳ���(v_table_name),
    ��ϵͳ�����к���ad_client_id�ķ����������һ������*/

    for v in (select distinct table_name from table_store) loop
        --����һ������
        v_table_name := upper(v.table_name);
        execute immediate 'alter table ' || p_username || '.' || v_table_name ||
                          ' add partition ' || v_part_table || ' values (' ||
                          v_ad_client_id || ')
          tablespace ' || v_addPart_table_name;
    end loop;
    --��������,��ִ̬����䲻�ܰ󶨱���

    /*exception
    when others then
        v_code    := sqlcode;
        v_message := sqlerrm;
        rollback;*/
end Ad_Client_Tsp_Create;
/

prompt
prompt Creating procedure AD_CLIENT_TSP_CREATE_BY_TBNAME
prompt =================================================
prompt
create or replace procedure Ad_Client_Tsp_Create_by_TbName(p_table_name in varchar2) as
    /*2008.04.07 zjf add
      ��һ���ض��ı�����һ������
      p_table_name ��Ӧ������Ҫ�����ı�
      p_ad_client_id ��Ӧ���Ǳ��е�ad_client_id��ֵ
    */
    v_code               number(10);
    v_message            varchar2(200);
    v_table_name         varchar2(100);
    v_part_table         varchar2(20);
    v_addPart_table_name varchar2(20);
    v_cnt                integer;

begin
    --��ȡ��Ҫ���ӷ����ı������
    v_table_name := upper(p_table_name); --'temp_ad_column';

    for v in (select distinct t.id from nds4.ad_client t) loop
        --��ȡ�������еķ���
        v_addPart_table_name := upper('g_2000q') || to_char(v.id);
        --����������ķ���
        v_part_table := 'P' || to_char(v.id);
    
        select count(*)
        into v_cnt
        from all_tab_partitions t
        where t.table_name = v_table_name and t.partition_name = v_part_table;
    
        if v_cnt = 0 then
            --����һ������
            execute immediate 'alter table nds4.' || v_table_name ||
                              ' add partition ' || v_part_table || ' values (' || v.id || ')
              tablespace ' || v_addPart_table_name;
        end if;
    
    end loop;

    v_message := '�����ɹ�';
    /*exception
    when others then
        v_code    := sqlcode;
        v_message := sqlerrm;
        rollback;*/
end Ad_Client_Tsp_Create_by_TbName;
/

prompt
prompt Creating procedure AD_CLIENT_TSP_RELOC_BY_TBNAME
prompt ================================================
prompt
create or replace procedure Ad_Client_Tsp_reloc_by_TbName(p_table_name in varchar2,
                                                          p_username in varchar2) as
    /*2008.04.07 zjf add
      ��һ���ض��ı�����ȫ������
      ��������Ѿ����ڣ��������ӣ�����������������
      p_username ��Ӧ�ĵ�¼oracleϵͳ���û���
    */
    v_table_name         varchar2(100); --��Ҫ�����ı���
    v_part_table         varchar2(20); --����������
    v_addPart_table_name varchar2(20); --tablespace����Ӧ�������ļ�����
    v_cnt                integer;
    --��������
    TYPE ObjectIDList IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;
    v_invoices ObjectIDList;

    query varchar2(400);

begin
    --�������������Ч�Եļ��
    if p_username is null then
        raise_application_error(-20001, 'δ��дϵͳ�û�������!');
    end if;
    if p_table_name is null then
        raise_application_error(-20001, 'δ��д��Ҫ���ӷ����ı�����');
    end if;

    --��ȡ��Ҫ���ӷ����ı������
    v_table_name := upper(p_table_name);
    query        := 'select distinct t.id from ' || p_username ||
                    '.ad_client t';
    execute immediate query BULK COLLECT
        INTO v_invoices;
    FOR i IN v_invoices.FIRST .. v_invoices.LAST LOOP
        --��ȡ�������еķ���
        v_addPart_table_name := upper('g_2000q') || to_char(v_invoices(i));
        --����������ķ���
        v_part_table := 'P' || to_char(v_invoices(i));
    
        select count(*)
        into v_cnt
        from all_tab_partitions t
        where t.table_name = v_table_name and t.partition_name = v_part_table;
    
        if v_cnt = 0 then
            --����һ������
            execute immediate 'alter table ' || p_username || '.' ||
                              v_table_name || ' add partition ' || v_part_table ||
                              ' values (' || v_invoices(i) || ')
              tablespace ' || v_addPart_table_name;
        end if;
    end loop;

    /*
        for v in (select distinct t.id from ad_client t) loop
            --��ȡ�������еķ���
            v_addPart_table_name := upper('g_2000q') || to_char(v.id);
            --����������ķ���
            v_part_table := 'P' || to_char(v.id);
        
            select count(*)
            into v_cnt
            from all_tab_partitions t
            where t.table_name = v_table_name and t.partition_name = v_part_table;
        
            if v_cnt = 0 then
                --����һ������
                execute immediate 'alter table nds4.' || v_table_name ||
                                  ' add partition ' || v_part_table || ' values (' || v.id || ')
                  tablespace ' || v_addPart_table_name;
            end if;
        
        end loop;
    */

end Ad_Client_Tsp_reloc_by_TbName;
/

prompt
prompt Creating procedure AD_CLIENT_TSP_RESIZE
prompt =======================================
prompt
create or replace procedure ad_client_tsp_resize(p_ad_client_id number,
                                                 p_re_size in varchar2,
                                                 p_flag in number,
                                                 p_dirpath in varchar2) as
    /*
       2008.03.28 zjf add
       �ض��������ļ��Ĵ�С
       p_tablespace tablespace������
       p_re_size tablespace�ض���Ĵ�С
       p_dirpath ������һ��dbf�ļ�ʱ��ָ����Ÿ�dbf�ļ���·��
       p_flag ��������
          1:dbf�ļ����иı���������
          2:�ڸ�tablespace������һ��dbf�ļ�(default:100M)
    */
    v_tablespace_path varchar2(100); --tablespace������λ��
    v_partit          varchar2(20); --tablespace���ļ���
    v_tbs_file_name   varchar2(200);

    v_ext_datafile varchar2(200); --������dbf���ļ�·��

    v_cnt integer;
begin
    --�������������Ч�Եļ��
    if p_flag is null then
        raise_application_error(-20001, 'δ�����tablespace�Ĳ�������!');
    end if;
    if p_ad_client_id is null then
        raise_application_error(-20001, 'δ���幫˾����Ӧ��ad_client_id!');
    end if;

    if p_flag = 2 then
        if p_dirpath is null then
            raise_application_error(-20001, 'δ������������dbf�ļ���·��');
        end if;
        if p_re_size is not null then
            raise_application_error(-20001, '����Ҫ���������ļ��Ĵ�С!');
        end if;
    elsif p_flag = 1 then
        if p_dirpath is not null then
            raise_application_error(-20001,
                                    '����Ҫ������������dbf�ļ���·��,�ò����Ǹı������Ĵ�С!');
        end if;
        if p_re_size is null then
            raise_application_error(-20001, 'δ���������ļ��Ĵ�С!');
        end if;
    else
        raise_application_error(-20001, '�����tablespace�Ĳ������Ͳ���ȷ!');
    end if;

    --�������з���������
    --��ȡ�����������������,���޸�
    v_partit := 'g_2000q' || to_char(p_ad_client_id);
    --��ȡTablespace������λ��
    /*v_tablespace_path := '''' || 'D:/oracle/product/10.2.0/oradata/orcl/' ||
    v_partit || '.dbf' || '''';*/

    /*��ʾ��չ���ݿ�ռ�������
    dbms_output.put_line(' ALTER DATABASE
                         DATAFILE ' ||
                         v_tablespace_path || ' RESIZE ' || p_re_size);
    */
    case p_flag
        when 1 then
            select count(*)
            into v_cnt
            from dba_data_files t
            where t.tablespace_name = upper(v_partit);
            --��ȡ���µķ���������        
            select g.file_name
            into v_tbs_file_name
            from (select rownum as num, t.*
                   --into v_tbs_file_name
                   from dba_data_files t
                   where t.tablespace_name = upper(v_partit)
                   order by t.file_id desc) g
            where g.num = v_cnt;
            /*
                      select t.file_name
                      into v_tbs_file_name
                      from dba_data_files t
                      where t.tablespace_name = upper(v_partit) and rownum = 1
                      order by t.relative_fno desc;
            */
            --Tablespace������λ��
            v_tablespace_path := '''' || v_tbs_file_name || '''';
            --���ܰ󶨶�̬����
            execute immediate ' ALTER DATABASE
                         DATAFILE ' ||
                              v_tablespace_path || ' RESIZE ' || p_re_size;
        when 2 then
            v_ext_datafile := '''' || p_dirpath || '/ext' ||
                              to_char(sysdate, 'yymmddhhmmss') || v_partit || '''';
            --���һ��datafile��tablespace��
            execute immediate ' ALTER TABLESPACE ' || v_partit ||
                              ' ADD datafile' || v_ext_datafile ||
                              ' SIZE 100M ';
            --alter tablespace G_2000Q6 add datafile 'D:\oracle\product\10.2.0\oradata\orcl\g_20001112.dbf' SIZE 50M;
        else
            raise_application_error(-20001,
                                    '��tablespace�Ĳ������Ͳ���ȷ ' || p_flag);
    end case;
/*exception
    when others then
        v_code    := sqlcode;
        v_message := sqlerrm;
        rollback;*/
end ad_client_tsp_resize;
/




create or replace procedure ad_client_tsp_calc_size(p_ad_client_id in number,
                                          p_size out number,
                                          p_str_size out varchar2) as
    /*
       @2008.04.30 zjf add
       ͳ��ÿ��ad_client_id����˾����ռ�õĿռ�
    */
    v_tablespace_name varchar2(100);
    v_mbytes_alloc    varchar2(50);
    v_mbytes_free     varchar2(50);
    v_partit          varchar2(20); --tablespace���ļ���
    v_cnt             int;
    v_init_alloc      number(10);
    v_init_free       number(10);
begin
    --�������з���������
    --��ȡ�����������������,���޸�
    v_partit := 'g_2000q' || to_char(p_ad_client_id);

    --�жϸ�ad_client_id����Ӧ�Ĺ�˾�Ƿ����
    select count(*)
    into v_cnt
    from all_tab_partitions t
    where t.tablespace_name = upper(v_partit) and rownum = 1;

    if v_cnt = 0 then
        raise_application_error(-20001,
                                p_ad_client_id || '����Ӧ���û�������,����!');
    end if;

    --ͳ�Ƹ�ad_client_id����Ӧ�Ĺ�˾��ռ�õ����ݿ�ռ�
    select b.tablespace_name, mbytes_alloc, mbytes_free
    into v_tablespace_name, v_mbytes_alloc, v_mbytes_free
    from (select round(sum(bytes) / 1024 / 1024) mbytes_free, tablespace_name
           from dba_free_space
           group by tablespace_name) a,
         (select round(sum(bytes) / 1024 / 1024) mbytes_alloc, tablespace_name
           from dba_data_files
           group by tablespace_name) b
    where a.tablespace_name(+) = b.tablespace_name and
          lower(b.tablespace_name) = v_partit;
    --ͳ��ģ��('P0')��ռ�õ����ݿ�ռ�
    select mbytes_alloc, mbytes_free
    into v_init_alloc, v_init_free
    from (select round(sum(bytes) / 1024 / 1024) mbytes_free, tablespace_name
           from dba_free_space
           group by tablespace_name) a,
         (select round(sum(bytes) / 1024 / 1024) mbytes_alloc, tablespace_name
           from dba_data_files
           group by tablespace_name) b
    where a.tablespace_name(+) = b.tablespace_name and
          lower(b.tablespace_name) = 'g_2000q0';
    --��ȡ�ù�˾��������ռ�õ����ݿ�ռ��С(��'M'Ϊ��λ)
    p_size := v_mbytes_alloc - (v_init_alloc - v_init_free) - v_mbytes_free;

    p_str_size := '���û��������Ѿ�ռ�õĿռ�Ϊ' || p_size || 'M';

end ad_client_tsp_calc_size;
/

spool off