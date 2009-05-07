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

