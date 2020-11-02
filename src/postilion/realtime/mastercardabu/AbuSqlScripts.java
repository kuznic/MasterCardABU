package postilion.realtime.mastercardabu;

public class AbuSqlScripts {
    /**
     * Script to create pc_cards_abu table
     * and other required scripts
     */
    public static final String TBL_PC_CARDS_ABU = "-- CREATE TABLE pc_cards_abu\n" +
            "IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[pc_cards_abu]') AND type in (N'U'))\n" +
            "BEGIN\n" +
            "DROP TABLE [dbo].[pc_cards_abu]\n" +
            "END\n" +
            "  BEGIN\n" +
            "   CREATE TABLE pc_cards_abu\n" +
            "   \t(\n" +
            "   \t[id] BIGINT IDENTITY(1,1),\n" +
            "   \t[issuer_nr]\tint NOT NULL,\n" +
            "   \t[pan]\tvarchar(66) NOT NULL,\n" +
            "   \t[seq_nr]\tchar(3) NOT NULL,\n" +
            "   \t[card_program]\tvarchar(20) NOT NULL,\n" +
            "    [default_account_type]\tchar(2) NOT NULL,\n" +
            "   \t[card_status]\tint NOT NULL,\n" +
            "   \t[card_custom_state]\tint NULL,\n" +
            "   \t[expiry_date]\tvarchar(4) NOT NULL,\n" +
            "   \t[hold_rsp_code]\tchar(2) NULL,\n" +
            "   \t[track2_value]\tvarchar(20) NULL,\n" +
            "   \t[track2_value_offset]\tint NULL,\n" +
            "   \t[pvki_or_pin_length]\tint NULL,\n" +
            "   \t[pvv_or_pin_offset] varchar(12) NULL,\n" +
            "   \t[pvv2_or_pin2_offset] varchar(12) NULL,\n" +
            "   \t[validation_data_question] varchar(50) NULL,\n" +
            "   \t[validation_data] varchar(50) NULL,\n" +
            "   \t[cardholder_rsp_info] varchar(50) NULL,\n" +
            "   \t[mailer_destination] int NOT NULL,\n" +
            "   \t[discretionary_data] varchar(19) NULL,\n" +
            "   \t[date_issued] datetime NULL,\n" +
            "   \t[date_activated] datetime NULL,\n" +
            "   \t[issuer_reference] varchar(20) NULL,\n" +
            "   \t[branch_code] varchar(10) NULL,\n" +
            "   \t[last_updated_date] datetime NOT NULL,\n" +
            "   \t[last_updated_user] varchar(20) NOT NULL,\n" +
            "   \t[customer_id] varchar(25) NULL,\n" +
            "   \t[batch_nr] int NULL,\n" +
            "   \t[company_card] int NULL,\n" +
            "   \t[date_deleted] datetime NULL,\n" +
            "   \t[pvki2_or_pin2_length] int NULL,\n" +
            "   \t[extended_fields] varchar(max) NULL,\n" +
            "   \t[expiry_day] char(2) NULL,\n" +
            "   \t[from_date] char(4) NULL,\n" +
            "   \t[from_day] char(2) NULL,\n" +
            "   \t[contactless_disc_data] varchar(19) NULL,\n" +
            "   \t[dcvv_key_index] int NULL,\n" +
            "   \t[pan_encrypted] varchar(70) NULL,\n" +
            "   \t[expiry_date_time] datetime NULL,\n" +
            "   \t[upload_status] bit NOT NULL DEFAULT 0,\n" +
            "   \t[account_chain] varchar(66) NULL,\n" +
            "   \t[account_hierarchy] int NULL,\n" +
            "   \t[closed] bit NOT NULL DEFAULT 0,\n" +
            "   \tCONSTRAINT pk_pc_cards_abu_col_id PRIMARY KEY (id)\n" +
            "   \t)\n" +
            "   ALTER TABLE [dbo].[pc_cards_abu] ADD  DEFAULT ('NIL') FOR [seq_nr]\n" +
            "\n" +
            "   ALTER TABLE [dbo].[pc_cards_abu] ADD  DEFAULT ('NIL') FOR [expiry_date]\n" +
            "  END;\n " +

            "IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id (N'[dbo].[get_all_active_cards]') AND OBJECTPROPERTY(id, N'IsProcedure') = 1)\n" +
            "BEGIN\n" +
            "DROP PROCEDURE [dbo].[get_all_active_cards]\n" +
            "END\n" +

            "IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id (N'[dbo].[get_closed_cards]') AND OBJECTPROPERTY(id, N'IsProcedure') = 1)\n" +
            "BEGIN\n" +
            "DROP PROCEDURE [dbo].[get_closed_cards]\n" +
            "END;\n" +

            "IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id (N'[dbo].[get_all_chained_cards]') AND OBJECTPROPERTY(id, N'IsProcedure') = 1)\n" +
            "BEGIN\n" +
            "DROP PROCEDURE [dbo].[get_all_chained_cards]\n" +
            "END;\n" +

            "IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id (N'[dbo].[update_status_chain_and_hierarchy]') AND OBJECTPROPERTY(id, N'IsProcedure') = 1)\n" +
            "BEGIN\n" +
            "DROP PROCEDURE [dbo].[update_status_chain_and_hierarchy]\n" +
            "END;\n" +

            "IF type_id('TVPCardUpdatesTableType') IS NOT NULL\n" +
            "BEGIN\n" +
            "DROP TYPE TVPCardUpdatesTableType " +
            "END;\n" +

            "CREATE TYPE TVPCardUpdatesTableType AS TABLE (\n" +
            "    id BIGINT,\n" +
            "    account_chain VARCHAR(66),\n" +
            "    account_hierarchy INT,\n" +
            "    upload_status VARCHAR(2))\n" +

            "IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id (N'[dbo].[update_closed_card_status]') AND OBJECTPROPERTY(id, N'IsProcedure') = 1)\n" +
            "BEGIN\n" +
            "DROP PROCEDURE [dbo].[update_closed_card_status]\n" +
            "END;\n" +

            "IF type_id('TVPClosedCardUpdatesTableType') IS NOT NULL\n" +
            "DROP TYPE TVPClosedCardUpdatesTableType\n" +

            "CREATE TYPE TVPClosedCardUpdatesTableType AS TABLE (\n" +
            "    id BIGINT,\n" +
            "    closed BIT\n" +
            "  )";

    /**
     * Script to create get_all_active_cards
     * stored procedure
     */

    public static final String SP_GET_ALL_ACTIVE_CARDS = "CREATE PROCEDURE get_all_active_cards   @bins VARCHAR(200), @page_num INT,\n" +
            "  @page_size INT AS\n" +
            "SELECT\n" +
            "  COUNT(*) AS count\n" +
            "FROM\n" +
            "  pc_cards_abu INNER JOIN pc_card_accounts\n" +
            "      ON\n" +
            "        pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "      WHERE\n" +
            "        pc_cards_abu.card_status = '1'\n" +
            "        AND pc_cards_abu.hold_rsp_code IS NULL\n" +
            "        AND pc_cards_abu.account_hierarchy IS NULL\n" +
            "        AND pc_cards_abu.expiry_date > (SELECT  convert(char(4), getdate(), 12))\n" +
            "        AND left( pc_cards_abu.pan,6) in (\n" +
            "            SELECT LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) 'Value'\n" +
            "        \tFROM\n" +
            "        \t(\n" +
            "        \t\t SELECT CAST ('<M>' + REPLACE(@bins, ',', '</M><M>') + '</M>' AS XML) AS Data\n" +
            "        \t) AS A\n" +
            "        \tCROSS APPLY Data.nodes ('/M') AS Split(a)\n" +
            "        )\n" +
            "SELECT\n" +
            "      pc_cards_abu.id,\n" +
            "      pc_cards_abu.pan,\n" +
            "      pc_cards_abu.expiry_date,\n" +
            "      pc_cards_abu.card_program\n" +
            "FROM\n" +
            "  pc_cards_abu INNER JOIN pc_card_accounts\n" +
            "      ON\n" +
            "        pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "      WHERE\n" +
            "        pc_cards_abu.card_status = '1'\n" +
            "        AND pc_cards_abu.hold_rsp_code IS NULL\n" +
            "        AND pc_cards_abu.account_hierarchy IS NULL\n" +
            "        AND pc_cards_abu.expiry_date > (SELECT  convert(char(4), getdate(), 12))\n" +
            "        AND left( pc_cards_abu.pan,6) in (\n" +
            "            SELECT LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) 'Value'\n" +
            "            FROM\n" +
            "            (\n" +
            "                 SELECT CAST ('<M>' + REPLACE(@bins, ',', '</M><M>') + '</M>' AS XML) AS Data\n" +
            "            ) AS A\n" +
            "            CROSS APPLY Data.nodes ('/M') AS Split(a)\n" +
            "        )\n" +
            "ORDER BY\n" +
            "  id OFFSET @page_size * (@page_num - 1) ROWS FETCH NEXT @page_size ROWS ONLY\n";

    /**
     * Script to create get_closed_cards
     * stored procedure
     */

    public static final String SP_GET_CLOSED_CARDS = "CREATE PROCEDURE get_closed_cards   @bins VARCHAR(200), @page_num INT,\n" +
            "  @page_size INT AS\n" +
            "SELECT\n" +
            "  COUNT(*) AS count\n" +
            "FROM\n" +
            "  pc_cards_abu INNER JOIN pc_card_accounts\n" +
            "      ON\n" +
            "        pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "      WHERE\n" +
            "         (pc_cards_abu.card_status != '1'\n" +
            "            OR pc_cards_abu.hold_rsp_code IN ('41','43','45')\n" +
            "            OR pc_cards_abu.expiry_date <= (SELECT  convert(char(4), getdate(), 12)))\n" +
            "            AND pc_cards_abu.closed = 0\n" +
            "            AND pc_cards_abu.account_hierarchy IS NOT NULL\n" +
            "            AND left( pc_cards_abu.pan,6) in (\n" +
            "                     SELECT LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) 'Value'\n" +
            "                        FROM\n" +
            "                        (\n" +
            "                             SELECT CAST ('<M>' + REPLACE(@bins, ',', '</M><M>') + '</M>' AS XML) AS Data\n" +
            "                        ) AS A\n" +
            "                        CROSS APPLY Data.nodes ('/M') AS Split(a)\n" +
            "                    )\n" +
            "SELECT\n" +
            "        pc_cards_abu.id,\n" +
            "        pc_cards_abu.pan,\n" +
            "        pc_cards_abu.expiry_date,\n" +
            "        pc_cards_abu.account_hierarchy\n" +
            "FROM\n" +
            "  pc_cards_abu INNER JOIN pc_card_accounts\n" +
            "      ON\n" +
            "        pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "      WHERE\n" +
            "        (pc_cards_abu.card_status != '1'\n" +
            "           OR pc_cards_abu.hold_rsp_code IN ('41','43','45')\n" +
            "           OR pc_cards_abu.expiry_date <= (SELECT  convert(char(4), getdate(), 12)))\n" +
            "           AND pc_cards_abu.closed = 0\n" +
            "           AND pc_cards_abu.account_hierarchy IS NOT NULL\n" +
            "           AND left( pc_cards_abu.pan,6) in (\n" +
            "                    SELECT LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) 'Value'\n" +
            "                   \tFROM\n" +
            "                   \t(\n" +
            "                   \t\t SELECT CAST ('<M>' + REPLACE(@bins, ',', '</M><M>') + '</M>' AS XML) AS Data\n" +
            "                   \t) AS A\n" +
            "                   \tCROSS APPLY Data.nodes ('/M') AS Split(a)\n" +
            "                   )\n" +
            "ORDER BY\n" +
            "  id OFFSET @page_size * (@page_num - 1) ROWS FETCH NEXT @page_size ROWS ONLY";
    /**
     * Script to create
     * get_all_chained_cards stored procedure
     */

    public static final String SP_GET_ALL_CHAINED_CARDS = "CREATE PROCEDURE get_all_chained_cards @bins VARCHAR(200), @page_num INT,\n" +
            "@page_size INT AS\n" +
            "SELECT\n" +
            "COUNT(*) AS count\n" +
            "FROM\n" +
            "  pc_cards_abu INNER JOIN pc_card_accounts\n" +
            "ON\n" +
            "  pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "WHERE\n" +
            "(\n" +
            "  pc_cards_abu.card_status = '1'\n" +
            "  AND pc_cards_abu.hold_rsp_code IS NULL\n" +
            "  AND pc_cards_abu.account_hierarchy IS NULL\n" +
            "  AND pc_cards_abu.expiry_date > (SELECT  convert(char(4), getdate(), 12))\n" +
            "  AND LEFT( pc_cards_abu.pan,6) in (\n" +
            "      SELECT LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) 'Value'\n" +
            "      FROM\n" +
            "      (\n" +
            "              SELECT CAST ('<M>' + REPLACE(@bins, ',', '</M><M>') + '</M>' AS XML) AS Data\n" +
            "      ) AS A\n" +
            "      CROSS APPLY Data.nodes ('/M') AS Split(a))\n" +
            ")\n" +
            ";WITH old_card_ids(date_activated, account_id) AS\n" +
            "(\n" +
            "  SELECT\n" +
            "      MAX(pc_cards_abu.date_activated),\n" +
            "      pc_card_accounts.account_id\n" +
            "  FROM\n" +
            "      pc_cards_abu INNER JOIN pc_card_accounts\n" +
            "  ON\n" +
            "      pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "  WHERE\n" +
            "      (\n" +
            "          (\n" +
            "              pc_cards_abu.card_status != '1'\n" +
            "              OR pc_cards_abu.hold_rsp_code IN ('41','43','45')\n" +
            "              OR pc_cards_abu.expiry_date <= (SELECT  convert(char(4), getdate(), 12))\n" +
            "          )\n" +
            "          AND pc_cards_abu.account_hierarchy IS NOT NULL\n" +
            "      )\n" +
            "  GROUP BY pc_card_accounts.account_id\n" +
            ")\n" +
            "SELECT\n" +
            "  *\n" +
            "FROM\n" +
            "  (\n" +
            "      SELECT\n" +
            "          id,\n" +
            "          pc_cards_abu.pan,\n" +
            "          pc_cards_abu.expiry_date,\n" +
            "          pc_cards_abu.card_program,\n" +
            "          pc_card_accounts.account_id\n" +
            "      FROM\n" +
            "          pc_cards_abu INNER JOIN pc_card_accounts\n" +
            "      ON\n" +
            "          pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "      WHERE\n" +
            "      (\n" +
            "          pc_cards_abu.card_status = '1'\n" +
            "          AND pc_cards_abu.hold_rsp_code IS NULL\n" +
            "          AND pc_cards_abu.account_hierarchy IS NULL\n" +
            "          AND pc_cards_abu.expiry_date > (SELECT  convert(char(4), getdate(), 12))\n" +
            "          AND LEFT( pc_cards_abu.pan,6) in (\n" +
            "              SELECT LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) 'Value'\n" +
            "              FROM\n" +
            "              (\n" +
            "                      SELECT CAST ('<M>' + REPLACE(@bins, ',', '</M><M>') + '</M>' AS XML) AS Data\n" +
            "              ) AS A\n" +
            "              CROSS APPLY Data.nodes ('/M') AS Split(a))\n" +
            "      )\n" +
            "  )\n" +
            "  new_cards\n" +
            "LEFT JOIN\n" +
            "  (\n" +
            "      SELECT\n" +
            "          pc_cards_abu.pan old_pan,\n" +
            "          pc_cards_abu.expiry_date old_expiry_date,\n" +
            "          pc_cards_abu.card_program old_card_program,\n" +
            "          pc_cards_abu.account_hierarchy old_account_hierarchy,\n" +
            "          pc_card_accounts.account_id\n" +
            "      FROM\n" +
            "          pc_cards_abu\n" +
            "      JOIN pc_card_accounts\n" +
            "      ON\n" +
            "          pc_cards_abu.pan = pc_card_accounts.pan\n" +
            "      JOIN old_card_ids\n" +
            "      ON\n" +
            "          pc_card_accounts.account_id = old_card_ids.account_id\n" +
            "          AND pc_cards_abu.date_activated = old_card_ids.date_activated\n" +
            "  )\n" +
            "  old_cards\n" +
            "ON new_cards.account_id = old_cards.account_id\n" +
            "ORDER BY\n" +
            "    id OFFSET @page_size * (@page_num - 1) ROWS FETCH NEXT @page_size ROWS ONLY";

    /**
     * Script to create
     * update_status_chain_and_hierarchy stored procedure
     */

    public static final String SP_UPDATE_STATUS_CHAIN_AND_HIERARCHY = "CREATE PROCEDURE update_status_chain_and_hierarchy @updates TVPCardUpdatesTableType READONLY AS\n " +
            "BEGIN\n" +
            "UPDATE pc_cards_abu\n" +
            "SET    account_chain = u.account_chain,\n" +
            "       account_hierarchy = u.account_hierarchy,\n" +
            "       upload_status = u.upload_status\n" +
            "FROM   @updates u\n" +
            "WHERE  u.id = pc_cards_abu.id\n" +
            "END;\n";
    /**
     * Script to create
     * update_closed_card_status stored procedure
     */

    public static final String SP_UPDATE_CLOSED_CARD_STATUS = "CREATE PROCEDURE update_closed_card_status @updates TVPClosedCardUpdatesTableType READONLY AS\n" +
            "BEGIN\n" +
            "UPDATE pc_cards_abu\n" +
            "SET    closed = u.closed\n" +
            "FROM   @updates u\n" +
            "WHERE  u.id = pc_cards_abu.id\n" +
            "END;\n";

    /**
     * Script to check if pc_cards view exists
     * and if the issuers for the ICA BINs are present in
     * the view
     */
    public static final String CHECK_PC_CARDS_VIEW = "IF EXISTS (select * from sys.objects where object_id = OBJECT_ID(N'[dbo].[pc_cards]')  AND type in (N'V'))\n" +
            "BEGIN\n" +
            "select distinct cp.card_prefix from pc_cards as pc\n" +
            "inner join pc_card_programs as cp\n" +
            "on pc.card_program = cp.card_program\n" +
            "where card_prefix in (?)\n" +
            "END;";

    /**
     * Script to check if pc_card_accounts view exists
     * and if the issuers for the ICA BINs are present in
     * the view
     */

    public static final String CHECK_PC_CARD_ACCOUNTS_VIEW = "IF EXISTS (select * from sys.objects where object_id = OBJECT_ID(N'[dbo].[pc_card_accounts]')  AND type in (N'V'))\n" +
            "BEGIN\n" +
            "select distinct cp.card_prefix from pc_card_accounts as ca\n" +
            "inner join pc_card_programs as cp\n" +
            "on cp.issuer_nr = ca.issuer_nr\n" +
            "where cp.card_prefix in (?)\n" +
            "END;";

    /**
     * Script to fetch issuers
     */

    public static final String FETCH_ISSUERS = "select issuer_nr from pc_card_programs where card_prefix in (?)";

    /**
     * Script to copy data from pc_cards to pc_cards_abu table in
     * test environment
     * Only a maximum of five records per BIN per script condition will
     * be copied over for the purpose of testing
     */

    public static final String POPULATE_PC_CARDS_ABU_TABLE_TEST = "INSERT INTO [dbo].[pc_cards_abu]\n" +
            "           ([issuer_nr]\n" +
            "           ,[pan]\n" +
            "           ,[seq_nr]\n" +
            "           ,[card_program]\n" +
            "           ,[default_account_type]\n" +
            "           ,[card_status]\n" +
            "           ,[card_custom_state]\n" +
            "           ,[expiry_date]\n" +
            "           ,[hold_rsp_code]\n" +
            "           ,[track2_value]\n" +
            "           ,[track2_value_offset]\n" +
            "           ,[pvki_or_pin_length]\n" +
            "           ,[pvv_or_pin_offset]\n" +
            "           ,[pvv2_or_pin2_offset]\n" +
            "           ,[validation_data_question]\n" +
            "           ,[validation_data]\n" +
            "           ,[cardholder_rsp_info]\n" +
            "           ,[mailer_destination]\n" +
            "           ,[discretionary_data]\n" +
            "           ,[date_issued]\n" +
            "           ,[date_activated]\n" +
            "           ,[issuer_reference]\n" +
            "           ,[branch_code]\n" +
            "           ,[last_updated_date]\n" +
            "           ,[last_updated_user]\n" +
            "           ,[customer_id]\n" +
            "           ,[batch_nr]\n" +
            "           ,[company_card]\n" +
            "           ,[date_deleted]\n" +
            "           ,[pvki2_or_pin2_length]\n" +
            "           ,[extended_fields]\n" +
            "           ,[expiry_day]\n" +
            "           ,[from_date]\n" +
            "           ,[from_day]\n" +
            "           ,[contactless_disc_data]\n" +
            "           ,[dcvv_key_index]\n" +
            "           ,[pan_encrypted]\n" +
            "           ,[expiry_date_time]\n" +
            "           ,[upload_status]\n" +
            "           ,[account_chain]\n" +
            "           ,[account_hierarchy]\n" +
            "           ,[closed])\n" +

            "SELECT top 2 pc.[issuer_nr]\n" +
            "      ,pc.[pan]\n" +
            "      ,pc.[seq_nr]\n" +
            "      ,pc.[card_program]\n" +
            "      ,pc.[default_account_type]\n" +
            "      ,pc.[card_status]\n" +
            "      ,[card_custom_state]\n" +
            "      ,[expiry_date]\n" +
            "      ,[hold_rsp_code]\n" +
            "      ,[track2_value]\n" +
            "      ,[track2_value_offset]\n" +
            "      ,[pvki_or_pin_length]\n" +
            "      ,[pvv_or_pin_offset]\n" +
            "      ,[pvv2_or_pin2_offset]\n" +
            "      ,[validation_data_question]\n" +
            "      ,[validation_data]\n" +
            "      ,[cardholder_rsp_info]\n" +
            "      ,[mailer_destination]\n" +
            "      ,[discretionary_data]\n" +
            "      ,[date_issued]\n" +
            "      ,[date_activated]\n" +
            "      ,[issuer_reference]\n" +
            "      ,[branch_code]\n" +
            "      ,pc.[last_updated_date]\n" +
            "      ,pc.[last_updated_user]\n" +
            "      ,[customer_id]\n" +
            "      ,[batch_nr]\n" +
            "      ,[company_card]\n" +
            "      ,pc.[date_deleted]\n" +
            "      ,[pvki2_or_pin2_length]\n" +
            "      ,[extended_fields]\n" +
            "      ,[expiry_day]\n" +
            "      ,[from_date]\n" +
            "      ,[from_day]\n" +
            "      ,[contactless_disc_data]\n" +
            "      ,[dcvv_key_index]\n" +
            "      ,pc.[pan_encrypted]\n" +
            "      ,[expiry_date_time]\n" +
            "       ,0\n" +
            "       ,null\n" +
            "       ,null\n" +
            "       ,0\n" +
            "  FROM [dbo].[pc_cards] (nolock) as pc\n" +
            "inner join pc_card_accounts as ca\n" +
            "on pc.pan = ca.pan\n" +
            "where pc.date_deleted is null and pc.card_status = 1 and pc.hold_rsp_code is null and substring (pc.pan,1,6) in (?)\n";

    /**
     * Script to copy data from pc_cards to pc_cards_abu table in
     * production  environment
     */
    public static final String POPULATE_PC_CARDS_ABU_TABLE_PROD = "INSERT INTO [dbo].[pc_cards_abu]\n" +
            "           ([issuer_nr]\n" +
            "           ,[pan]\n" +
            "           ,[seq_nr]\n" +
            "           ,[card_program]\n" +
            "           ,[default_account_type]\n" +
            "           ,[card_status]\n" +
            "           ,[card_custom_state]\n" +
            "           ,[expiry_date]\n" +
            "           ,[hold_rsp_code]\n" +
            "           ,[track2_value]\n" +
            "           ,[track2_value_offset]\n" +
            "           ,[pvki_or_pin_length]\n" +
            "           ,[pvv_or_pin_offset]\n" +
            "           ,[pvv2_or_pin2_offset]\n" +
            "           ,[validation_data_question]\n" +
            "           ,[validation_data]\n" +
            "           ,[cardholder_rsp_info]\n" +
            "           ,[mailer_destination]\n" +
            "           ,[discretionary_data]\n" +
            "           ,[date_issued]\n" +
            "           ,[date_activated]\n" +
            "           ,[issuer_reference]\n" +
            "           ,[branch_code]\n" +
            "           ,[last_updated_date]\n" +
            "           ,[last_updated_user]\n" +
            "           ,[customer_id]\n" +
            "           ,[batch_nr]\n" +
            "           ,[company_card]\n" +
            "           ,[date_deleted]\n" +
            "           ,[pvki2_or_pin2_length]\n" +
            "           ,[extended_fields]\n" +
            "           ,[expiry_day]\n" +
            "           ,[from_date]\n" +
            "           ,[from_day]\n" +
            "           ,[contactless_disc_data]\n" +
            "           ,[dcvv_key_index]\n" +
            "           ,[pan_encrypted]\n" +
            "           ,[expiry_date_time]\n" +
            "           ,[upload_status]\n" +
            "           ,[account_chain]\n" +
            "           ,[account_hierarchy]\n" +
            "           ,[closed])\n" +

            "SELECT pc.[issuer_nr]\n" +
            "      ,pc.[pan]\n" +
            "      ,pc.[seq_nr]\n" +
            "      ,pc.[card_program]\n" +
            "      ,pc.[default_account_type]\n" +
            "      ,pc.[card_status]\n" +
            "      ,pc.[card_custom_state]\n" +
            "      ,pc.[expiry_date]\n" +
            "      ,pc.[hold_rsp_code]\n" +
            "      ,[track2_value]\n" +
            "      ,[track2_value_offset]\n" +
            "      ,[pvki_or_pin_length]\n" +
            "      ,[pvv_or_pin_offset]\n" +
            "      ,[pvv2_or_pin2_offset]\n" +
            "      ,[validation_data_question]\n" +
            "      ,[validation_data]\n" +
            "      ,[cardholder_rsp_info]\n" +
            "      ,[mailer_destination]\n" +
            "      ,[discretionary_data]\n" +
            "      ,[date_issued]\n" +
            "      ,[date_activated]\n" +
            "      ,[issuer_reference]\n" +
            "      ,[branch_code]\n" +
            "      ,pc.[last_updated_date]\n" +
            "      ,pc.[last_updated_user]\n" +
            "      ,[customer_id]\n" +
            "      ,[batch_nr]\n" +
            "      ,[company_card]\n" +
            "      ,pc.[date_deleted]\n" +
            "      ,[pvki2_or_pin2_length]\n" +
            "      ,[extended_fields]\n" +
            "      ,[expiry_day]\n" +
            "      ,[from_date]\n" +
            "      ,[from_day]\n" +
            "      ,[contactless_disc_data]\n" +
            "      ,[dcvv_key_index]\n" +
            "      ,pc.[pan_encrypted]\n" +
            "      ,[expiry_date_time]\n" +
            "      ,0\n" +
            "      ,null\n" +
            "      ,null\n" +
            "      ,0\n" +
            "  FROM [dbo].[pc_cards] (nolock) as pc\n" +
            "inner join pc_card_accounts as ca\n" +
            "on pc.pan = ca.pan\n" +
            "where pc.date_deleted is null and pc.card_status = 1 and pc.hold_rsp_code is null and substring (pc.pan,1,6) in (?)\n";

    /**
     * Script to create first index on
     * pc_cards_abu table
     */

    public static final String IX_PC_CARDS_ABU = "CREATE NONCLUSTERED INDEX [ix_pc_cards_abu]\n" +
            "ON [dbo].[pc_cards_abu] ([pan],[seq_nr])\n" +
            "INCLUDE ([id])";

    /**
     * Script to create first index on
     * pc_cards_abu table
     */


    public static final String IX_PC_CARDS_ABU_2 = "CREATE NONCLUSTERED INDEX [ix_pc_cards_abu_2]\n" +
            "ON [dbo].[pc_cards_abu] ([card_status],[hold_rsp_code],[account_hierarchy],[expiry_date])\n";

    public static final String TR_PC_CARDS_PART_1 = "CREATE TRIGGER [dbo].[tr_at_update_mastercard_abu_";
    public static final String TR_PC_CARDS_PART_2 = "ON [dbo].[pc_cards_";
    public static final String TR_PC_CARDS_PART_3 = " after update AS\n" +
            "BEGIN \n" +
            "set nocount on;\n" +
            "UPDATE pc_cards_abu SET card_status = i.card_status\n" +
            "from pc_cards_abu as pcb\n" +
            "inner join inserted i  on pcb.pan= i.pan and pcb.seq_nr = i.seq_nr;\n" +
            "\n" +
            "update pc_cards_abu set hold_rsp_code = i.hold_rsp_code\n" +
            "from pc_cards_abu as pcb\n" +
            "inner join inserted i  on pcb.pan= i.pan and pcb.seq_nr = i.seq_nr;\n" +
            "end";

    /**
     * Used to drop the abu triggers from the issuer table
     */
    public static final String DROP_TRIGGERS_PART_1 = "IF EXISTS (select * from sys.objects where object_id = OBJECT_ID(N'[dbo].[tr_at_update_mastercard_abu_";
    public static final String DROP_TRIGGERS_PART_2 ="')  AND type in (N'TR'))\n" +
            "BEGIN\n" +
            "DROP TRIGGER ";
    public static final String DROP_TRIGGERS_PART_3 = "\nEND\n" +
            "ELSE\n" +
            "RETURN";


    public static final String SP_INSERT_NEW_RECORDS_IN_ABU_TABLE_PART_1 = "create procedure abu_insert_new_records\n" +
            "as \n" +
            "declare @abu_last_updated_date  varchar(15)\n" +
            "set @abu_last_updated_date =(select top 1 CONVERT(VARcHAR(10), last_updated_date, 111) from pc_cards_abu nolock\n" +
            "order by last_updated_date  desc\n" +
            ")\n" +
            "INSERT INTO [dbo].[pc_cards_abu]\n" +
            "           ([issuer_nr]\n" +
            "           ,[pan]\n" +
            "           ,[seq_nr]\n" +
            "           ,[card_program]\n" +
            "           ,[default_account_type]\n" +
            "           ,[card_status]\n" +
            "           ,[card_custom_state]\n" +
            "           ,[expiry_date]\n" +
            "           ,[hold_rsp_code]\n" +
            "           ,[track2_value]\n" +
            "           ,[track2_value_offset]\n" +
            "           ,[pvki_or_pin_length]\n" +
            "           ,[pvv_or_pin_offset]\n" +
            "           ,[pvv2_or_pin2_offset]\n" +
            "           ,[validation_data_question]\n" +
            "           ,[validation_data]\n" +
            "           ,[cardholder_rsp_info]\n" +
            "           ,[mailer_destination]\n" +
            "           ,[discretionary_data]\n" +
            "           ,[date_issued]\n" +
            "           ,[date_activated]\n" +
            "           ,[issuer_reference]\n" +
            "           ,[branch_code]\n" +
            "           ,[last_updated_date]\n" +
            "           ,[last_updated_user]\n" +
            "           ,[customer_id]\n" +
            "           ,[batch_nr]\n" +
            "           ,[company_card]\n" +
            "           ,[date_deleted]\n" +
            "           ,[pvki2_or_pin2_length]\n" +
            "           ,[extended_fields]\n" +
            "           ,[expiry_day]\n" +
            "           ,[from_date]\n" +
            "           ,[from_day]\n" +
            "           ,[contactless_disc_data]\n" +
            "           ,[dcvv_key_index]\n" +
            "           ,[pan_encrypted]\n" +
            "           ,[expiry_date_time]\n" +
            "           ,[upload_status]\n" +
            "           ,[account_chain]\n" +
            "           ,[account_hierarchy]\n" +
            "           ,[closed])\n" +

            "SELECT [issuer_nr]\n" +
            "      ,[pan]\n" +
            "      ,[seq_nr]\n" +
            "      ,[card_program]\n" +
            "      ,[default_account_type]\n" +
            "      ,[card_status]\n" +
            "      ,[card_custom_state]\n" +
            "      ,[expiry_date]\n" +
            "      ,[hold_rsp_code]\n" +
            "      ,[track2_value]\n" +
            "      ,[track2_value_offset]\n" +
            "      ,[pvki_or_pin_length]\n" +
            "      ,[pvv_or_pin_offset]\n" +
            "      ,[pvv2_or_pin2_offset]\n" +
            "      ,[validation_data_question]\n" +
            "      ,[validation_data]\n" +
            "      ,[cardholder_rsp_info]\n" +
            "      ,[mailer_destination]\n" +
            "      ,[discretionary_data]\n" +
            "      ,[date_issued]\n" +
            "      ,[date_activated]\n" +
            "      ,[issuer_reference]\n" +
            "      ,[branch_code]\n" +
            "      ,[last_updated_date]\n" +
            "      ,[last_updated_user]\n" +
            "      ,[customer_id]\n" +
            "      ,[batch_nr]\n" +
            "      ,[company_card]\n" +
            "      ,[date_deleted]\n" +
            "      ,[pvki2_or_pin2_length]\n" +
            "      ,[extended_fields]\n" +
            "      ,[expiry_day]\n" +
            "      ,[from_date]\n" +
            "      ,[from_day]\n" +
            "      ,[contactless_disc_data]\n" +
            "      ,[dcvv_key_index]\n" +
            "      ,[pan_encrypted]\n" +
            "      ,[expiry_date_time]\n" +
            "      ,0\n" +
            "      ,null\n" +
            "      ,null\n" +
            "      ,0\n" +
            "  FROM [dbo].[pc_cards]\n" +
            "where date_deleted is null  and substring(pan,1,6) in (";

    public static final String SP_INSERT_NEW_RECORDS_IN_ABU_TABLE_PART_2 = ") and last_updated_date > @abu_last_updated_date";

    public static final String DROP_SP_INSERT_NEW_RECORDS_IN_ABU_TABLE =  "IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id (N'[dbo].[abu_insert_new_records]') AND OBJECTPROPERTY(id, N'IsProcedure') = 1)\n" +
            "BEGIN\n" +
            "DROP PROCEDURE [dbo].[abu_insert_new_records]\n" +
            "END;\n" ;

    /**
     * This script is used to populate pc_cards_abu table
     * with new records for reason code N
     */

    public static final String POPULATE_PC_CARDS_ABU_TABLE_TEST_FOR_CODE_N = "with pan_cte as \n" +
            "(" +
            "select * from pc_cards (nolock) where \n" +
            "pan not in (select pan from pc_cards_abu)\n" +
            ")\n" +
            "insert into pc_cards_abu\n" +
            "SELECT top 2 [issuer_nr] \n" +
            "                  ,[pan] \n" +
            "                  ,[seq_nr] \n" +
            "                  ,[card_program] \n" +
            "                  ,[default_account_type] \n" +
            "                  ,[card_status] \n" +
            "                  ,[card_custom_state] \n" +
            "                  ,[expiry_date] \n" +
            "                  ,[hold_rsp_code] \n" +
            "                  ,[track2_value] \n" +
            "                  ,[track2_value_offset] \n" +
            "                  ,[pvki_or_pin_length] \n" +
            "                  ,[pvv_or_pin_offset] \n" +
            "                  ,[pvv2_or_pin2_offset] \n" +
            "                  ,[validation_data_question] \n" +
            "                  ,[validation_data] \n" +
            "                  ,[cardholder_rsp_info] \n" +
            "                  ,[mailer_destination] \n" +
            "                  ,[discretionary_data] \n" +
            "                  ,[date_issued] \n" +
            "                  ,[date_activated] \n" +
            "                  ,[issuer_reference] \n" +
            "                  ,[branch_code] \n" +
            "                  ,[last_updated_date] \n" +
            "                  ,[last_updated_user] \n" +
            "                  ,[customer_id] \n" +
            "                  ,[batch_nr] \n" +
            "                  ,[company_card] \n" +
            "                  ,[date_deleted] \n" +
            "                  ,[pvki2_or_pin2_length] \n" +
            "                  ,[extended_fields] \n" +
            "                  ,[expiry_day] \n" +
            "                  ,[from_date] \n" +
            "                  ,[from_day] \n" +
            "                  ,[contactless_disc_data] \n" +
            "                  ,[dcvv_key_index] \n" +
            "                  ,[pan_encrypted] \n" +
            "                  ,[expiry_date_time] \n" +
            "                  ,0 \n" +
            "                  ,null \n" +
            "                  ,null \n" +
            "                  ,0 \n" +
            "                  from pan_cte where substring(pan,1,6) in (?)";


    /**
     * Fetch top two records from pc_cards_abu table
     */
    public static final String FETCH_RECORDS_FROM_PC_CARDS_ABU = "select top 2 [issuer_nr],[pan] from [dbo].[pc_cards_abu] (nolock) where account_chain is not null";

    /**
     * Fetch closed record from pc_cards_abu table
     */
    public static final String FETCH_CLOSED_RECORD = "SELECT TOP 1 ISSUER_NR,PAN FROM PC_CARDS_ABU WHERE HOLD_RSP_CODE = 41 AND CLOSED = 1";

    public static final String INSERT_INTO_PC_CARDS_ISSUER_TABLE = "(issuer_nr,pan,seq_nr,card_program,default_account_type,card_status,expiry_date,hold_rsp_code," +
            "pvki_or_pin_length,pvv_or_pin_offset,mailer_destination,discretionary_data," +
            "date_issued,date_activated,branch_code,last_updated_date,last_updated_user,customer_id,company_card,pvki2_or_pin2_length)\n" +
            "select issuer_nr,?,seq_nr,card_program,default_account_type,card_status,expiry_date,null,pvki_or_pin_length,pvv_or_pin_offset,mailer_destination,discretionary_data,getdate(),GETDATE(),branch_code,GETDATE(),last_updated_user,customer_id,company_card,pvki2_or_pin2_length\n" +
            "from pc_cards_abu where pan = ?";

    public static final String INSERT_INTO_PC_CARD_ACCOUNTS_ISSUER_TABLE = "(issuer_nr,pan,seq_nr,account_id,account_type_nominated,account_type_qualifier,last_updated_date," +
            "last_updated_user,account_type,date_deleted)\n" +
            "select issuer_nr,?,seq_nr,account_id,account_type_nominated,account_type_qualifier,getdate(),last_updated_user,account_type,date_deleted\n" +
            "from pc_card_accounts where pan = ?";

    public static final String INSERT_INTO_PC_CARDS_ABU_TABLE = "insert into pc_cards_abu (issuer_nr,pan,seq_nr,card_program,default_account_type,card_status," +
            "expiry_date,hold_rsp_code,pvki_or_pin_length,pvv_or_pin_offset,mailer_destination,discretionary_data,date_issued,date_activated,branch_code," +
            "last_updated_date,last_updated_user,customer_id,company_card,pvki2_or_pin2_length,upload_status,account_chain,account_hierarchy,closed)\n" +
            "select issuer_nr,pan,seq_nr,card_program,default_account_type,card_status," +
            "expiry_date,null,pvki_or_pin_length,pvv_or_pin_offset,mailer_destination,discretionary_data,date_issued,date_activated,branch_code," +
            "last_updated_date,last_updated_user,customer_id,company_card,pvki2_or_pin2_length,0 ,null,null,0"+
            " from pc_cards where pan = ?";



}


