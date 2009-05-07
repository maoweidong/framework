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
    */

end Ad_Client_Tsp_reloc_by_TbName;
/

