@echo off
COLOR 0b
title [L2jEnergy] Database Installer

REM ############################################
REM ## You can change here your own DB params ##
REM ############################################
REM MYSQL BIN PATH
set mysqlBinPath=D:\Program Files\MariaDB 10.0\bin

REM LOGINSERVER
set lsuser=root
set lspass=
set lsdb=l2jenergy
set lshost=localhost

REM GAMESERVER
set gsuser=root
set gspass=
set gsdb=l2jenergy
set gshost=localhost
REM ############################################

set mysqldumpPath="%mysqlBinPath%\mysqldump"
set mysqlPath="%mysqlBinPath%\mysql"

REM ####################################### :main_menu
:main_menu
cls
echo.
echo.	#############################################
echo.	#      L2jEnergy database installation      #
echo.	#############################################
echo.
echo.   (0) Install tables LoginServer
echo.   (1) Install tables GameServer
echo.   (2) Optimize tables Server
echo.   (3) Install tables custom
echo.   (4) Install tables mods
echo.   (q) Exit
echo.
set button=x
set /p button=   Enter your choice ?:
if /i %button%==0 goto Install_Login_Server_menu
if /i %button%==1 goto Install_Game_Server_menu
if /i %button%==2 goto optimize_login_game_db
if /i %button%==3 goto install_custom_db
if /i %button%==4 goto install_mods_db
if /i %button%==q goto end
goto main_menu

REM ######################################## :Install_Login_Server_menu
:Install_Login_Server_menu
cls
echo.   Install LoginServer tables
echo.
echo.   ###################################
echo.   #   Install LoginServer tables    #
echo.   ###################################
echo.
echo.   (i) Installing LoginServer Tables.
echo.       ATTENTION:(Tables) account_data, accounts, accounts_ipauth, gameservers! Will be deleted!
echo.   (m) Main menu
echo.   (q) Exit
echo.
set button=x
set /p button=   Enter your choice  ?:
if /i %button%==i goto Install_Login_Server
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto Install_Login_Server_menu

REM ######################################## :Install_Game_Server_menu
:Install_Game_Server_menu
cls
echo.
echo.   ################################
echo.   #   Install GameServer tables  #
echo.   ################################
echo.
echo.   (i) Installing GameServer Tables.
echo.       ATTENTION: The tables of the game server will be deleted!
echo.   (m) Main menu
echo.   (q) Exit
echo.
set button=x
set /p button=   Enter your choice  ?:
if /i %button%==i goto Install_Game_Server
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto Install_Game_Server_menu

REM ######################################## :optimize_login_game_db
:optimize_login_game_db
cls
echo.
echo.   ####################################
echo.   #  Update tables Login and Game Server  #
echo.   ####################################
echo.
echo.   (o) Optimize tables LoginServer and GameServer
echo.   (m) Main menu
echo.   (q) Exit
echo.
set button=x
set /p button=   Enter your choice  ?:
if /i %button%==o goto optimize_db
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto optimize_login_game_db

REM ######################################## :install_custom_db
:install_custom_db
cls
echo.
echo.   #####################################
echo.   #      Installing Custom Tables     #
echo.   #####################################
echo.
echo.   (u) Installing Custom Tables
echo.   (m) Main menu
echo.   (q) Exit
echo.
set button=x
set /p button=   Enter your choice ?:
if /i %button%==u goto installcustom_db
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto install_mods_db

REM ######################################## :install_mods_db
:install_mods_db
cls
echo.
echo.   #####################################
echo.   #      Installing Mods Tables       #
echo.   #####################################
echo.
echo.   (u) Installing Mods Tables
echo.   (m) Main menu
echo.   (q) Exit
echo.
set button=x
set /p button=   Enter your choice ?:
if /i %button%==u goto installmods_db
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto install_mods_db

REM ######################################## :Install_Login_Server
:Install_Login_Server
echo.
echo Deleting LoginServer tables.
%mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < ls_cleanup.sql
echo Done.
echo.
echo Installing empty LoginServer tables.
%mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < sql/login/account_data.sql
%mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < sql/login/accounts_ipauth.sql
%mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < sql/login/accounts.sql
%mysqlPath% -h %gshost% -u %lsuser% --password=%lspass% -D %lsdb% < sql/login/gameservers.sql
echo.
echo.   :. Installation table LoginServer finished. 
echo.
pause
goto main_menu

REM ######################################## :Install_Game_Server
:Install_Game_Server
echo.
echo Deleting GameServer tables.
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < gs_cleanup.sql
echo Done.
echo.
echo Installing empty GameServer tables.
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/account_gsdata.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/account_premium.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/airships.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/announcements.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/auction.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/auction_bid.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/auction_watch.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/bbs_buffer.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/bbs_favorite.sql 
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/bbs_mail.sql 
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/bbs_memo.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/bbs_teleport.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/bot_reported_char_data.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/buylists.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/castle.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/castle_doorupgrade.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/castle_functions.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/castle_manor_procure.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/castle_manor_production.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/castle_siege_guards.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/castle_trapupgrade.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_post_friends.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_friends.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_hennas.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_instance_time.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_item_mall_transactions.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_item_reuse_save.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_macroses.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_minigame_score.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_offline_trade.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_offline_trade_items.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_pet_skills_save.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_premium_items.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_quest_global_data.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_quests.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_raid_points.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_recipebook.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_recipeshoplist.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_reco_bonus.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_shortcuts.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_skills.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_skills_save.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_subclasses.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_summon_skills_save.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_summons.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_tpbookmark.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_ui_actions.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_ui_categories.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_variables.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/character_votes.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/characters.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clan_data.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clan_notices.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clan_privs.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clan_skills.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clan_subpledges.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clan_wars.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clanhall.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clanhall_functions.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clanhall_siege_attackers.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/clanhall_siege_guards.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/crests.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/cursed_weapons.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/dimensional_rift.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/fishing_championship.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/fort.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/fort_doorupgrade.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/fort_functions.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/fort_siege_guards.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/fort_spawnlist.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/fortsiege_clans.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/forums.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/four_sepulchers_spawnlist.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/games.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/global_tasks.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/global_variables.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/grandboss_data.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/grandboss_list.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/herb_droplist_groups.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/heroes.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/heroes_diary.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/item_attributes.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/item_auction.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/item_auction_bid.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/item_elementals.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/items.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/itemsonground.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/locations.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/merchant_lease.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/messages.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/npc_buffer.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/olympiad_data.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/olympiad_fights.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/olympiad_nobles.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/olympiad_nobles_eom.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/pccafe_coupons.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/petition_feedback.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/pets.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/pets_skills.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/posts.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/punishments.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/quest_global_data.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/raidboss_spawnlist.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/rainbowsprings_attacker_list.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/random_spawn.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/random_spawn_loc.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/rim_kamaloka.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/seven_signs.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/seven_signs_festival.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/seven_signs_status.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/siegable_clanhall.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/siegable_hall_flagwar_attackers.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/siegable_hall_flagwar_attackers_members.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/siege_clans.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/spawnlist.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/teleport.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/territories.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/territory_registrations.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/territory_spawnlist.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/game/topic.sql

echo.
echo.   :. Installation table GameServer finished. 
echo.
pause
goto installcustom_db

REM ######################################## :installcustom_db
:installcustom_db
echo.
echo Installing custom tables.
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/custom/custom_npc_buffer.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/custom/custom_spawnlist.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/custom/custom_teleport.sql
echo.
echo.   : Installation table custom finished. 
echo.
pause
goto installmods_db

REM ######################################## :installmods_db
:installmods_db
echo.
echo Installing Mods tables.
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/mods/mods_wedding.sql
echo.
echo.   : Installation table Mods finished. 
echo.
pause
goto main_menu

REM ######################################## :optimize_db
:optimize_db
echo.
echo optimize tables start.
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/optimize/ls_optimize.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < sql/optimize/gs_optimize.sql
echo.
echo.   : Optimize tables finished. 
echo.
pause
goto main_menu