1.��DBCA�����������ݿ�"RMAN"���޸�Ŀ�����ݿ�Ϊ���鵵ģʽ��Ҳ��ֱ������ ������.BAT��
 
2.  ������ʽ����RMAN���ݼƻ�֮ǰ����һ��RMAN���ݼ�¼������RMAN����ʱ���ܻᱨ�Ҳ������ֹ�ɾ���Ĺ鵵��־��
          rman>crosscheck archivelog all;
          rman>delete expired archivelog all;
          rman>crosscheck backup;
          rman>delete expired backup;
          ��ֱ������  
                     ����ı���¼.bat  ���޸�ʵ������
   
3.�ڿ������--����ƻ��������Ӧ������ƻ����磺
	ÿ�����һ��������12:00���ݿ�ȫ��        ������Rman_Backup_Full.bat��
	ÿ������8:30�����ݿ�0������              ������Rman_Backup_Level0.bat��
	ÿ��һ�������ġ��塢����8:30��2�����챸�ݣ�����Rman_Backup_Level2.bat��
        ÿ������8:30�����ݿ�1�����챸��          ������Rman_Backup_Level1.bat��

4.�����ı��༭�����޸ı���.bat,��ԭ.bat�Ͳ���rcv�е�ʵ�����ݿ�����
��sys/oracle@orcl����,���ʹ��catalogĿ¼���ɼ���catalog������
         PWD@��ʾ
re_back.rcv
rman_backup_1.rcv
Rman_Backup_Full.rcv
Rman_Backup_Full0.rcv
Rman_Backup_Full1.rcv
Rman_Backup_Full2.rcv
��ԭ.bat
����.bat
5.�洢Ŀ¼Ϊ
E:\oracle\oradata\cc\   �����Ŀ¼
E:\bk                    ���ݴ洢Ŀ¼