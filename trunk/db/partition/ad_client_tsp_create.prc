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

