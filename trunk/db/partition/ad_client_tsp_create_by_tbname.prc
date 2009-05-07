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

