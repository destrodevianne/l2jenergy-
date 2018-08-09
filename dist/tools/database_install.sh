trap finish 2

configure() {

# Loginserver
LSDBHOST="localhost"
LSDB="l2jenergy"
LSUSER="root"
LSPASS=""

# Gameserver
GSDBHOST="localhost"
GSDB="l2jenergy"
GSUSER="root"
GSPASS=""

echo "                   L2jEnergy database installation"
echo "                   _______________________________"
echo ""

MYSQLDUMPPATH=`which mysqldump 2>/dev/null`
MYSQLPATH=`which mysql 2>/dev/null`
if [ $? -ne 0 ]; then
echo "We were unable to find MySQL binaries on your path"
while :
 do
  echo -ne "\nPlease enter MySQL binaries directory (no trailing slash): "
  read MYSQLBINPATH
    if [ -e "$MYSQLBINPATH" ] && [ -d "$MYSQLBINPATH" ] && [ -e "$MYSQLBINPATH/mysqldump" ] && [ -e "$MYSQLBINPATH/mysql" ]; then
       MYSQLDUMPPATH="$MYSQLBINPATH/mysqldump"
       MYSQLPATH="$MYSQLBINPATH/mysql"
       break
    else
       echo "The data you entered is invalid. Please verify and try again."
       exit 1
    fi
 done
fi
MYL="$MYSQLPATH -h $LSDBHOST -u $LSUSER --password=$LSPASS -D $LSDB"
MYG="$MYSQLPATH -h $GSDBHOST -u $GSUSER --password=$GSPASS -D $GSDB"

echo "OPTIONS : (f) full install, it will destroy all."
echo "          (s) skip characters data, it will install only static server tables."
echo ""
echo -ne "Installation type: (f) full, (s) skip or (q) quit? "

read PROMPT
case "$PROMPT" in
	"f"|"F") fullinstall; upgradeinstall I;;
	"s"|"S") skip; upgradeinstall U;;
	"q"|"Q") finish;;
	*) configure;;
esac
}

fullinstall(){
echo "Deleting all tables for new content."
$MYG < ls_cleanup.sql &> /dev/null
$MYG < gs_cleanup.sql &> /dev/null
}

skip(){
echo "Deleting all gameserver tables for new content."
$MYG < gs_cleanup.sql &> /dev/null
}

upgradeinstall(){
if [ "$1" == "I" ]; then 
echo "Installling new content."
else
echo "Upgrading gameserver content"
fi

$MYL < sql/login/account_data.sql &> /dev/null
$MYL < sql/login/accounts.sql &> /dev/null
$MYL < sql/login/accounts_ipauth.sql &> /dev/null
$MYL < sql/login/gameservers.sql &> /dev/null

$MYL < sql/game/account_gsdata.sql &> /dev/null
$MYL < sql/game/airships.sql &> /dev/null
$MYL < sql/game/announcements.sql &> /dev/null
$MYL < sql/game/auction.sql &> /dev/null
$MYL < sql/game/auction_bid.sql &> /dev/null
$MYL < sql/game/auction_watch.sql &> /dev/null
$MYL < sql/game/bbs_favorite.sql  &> /dev/null
$MYL < sql/game/bbs_mail.sql  &> /dev/null
$MYL < sql/game/bbs_memo.sql  &> /dev/null
$MYL < sql/game/bot_reported_char_data.sql &> /dev/null
$MYL < sql/game/buylists.sql &> /dev/null
$MYL < sql/game/castle.sql &> /dev/null
$MYL < sql/game/castle_doorupgrade.sql &> /dev/null
$MYL < sql/game/castle_functions.sql &> /dev/null
$MYL < sql/game/castle_manor_procure.sql &> /dev/null
$MYL < sql/game/castle_manor_production.sql &> /dev/null
$MYL < sql/game/castle_siege_guards.sql &> /dev/null
$MYL < sql/game/castle_trapupgrade.sql &> /dev/null
$MYL < sql/game/character_contacts.sql &> /dev/null
$MYL < sql/game/character_friends.sql &> /dev/null
$MYL < sql/game/character_hennas.sql &> /dev/null
$MYL < sql/game/character_instance_time.sql &> /dev/null
$MYL < sql/game/character_item_mall_transactions.sql &> /dev/null
$MYL < sql/game/character_item_reuse_save.sql &> /dev/null
$MYL < sql/game/character_macroses.sql &> /dev/null
$MYL < sql/game/character_minigame_score.sql &> /dev/null
$MYL < sql/game/character_offline_trade.sql &> /dev/null
$MYL < sql/game/character_offline_trade_items.sql &> /dev/null
$MYL < sql/game/character_pet_skills_save.sql &> /dev/null
$MYL < sql/game/character_premium_items.sql &> /dev/null
$MYL < sql/game/character_quest_global_data.sql &> /dev/null
$MYL < sql/game/character_quests.sql &> /dev/null
$MYL < sql/game/character_raid_points.sql &> /dev/null
$MYL < sql/game/character_recipebook.sql &> /dev/null
$MYL < sql/game/character_recipeshoplist.sql &> /dev/null
$MYL < sql/game/character_reco_bonus.sql &> /dev/null
$MYL < sql/game/character_shortcuts.sql &> /dev/null
$MYL < sql/game/character_skills.sql &> /dev/null
$MYL < sql/game/character_skills_save.sql &> /dev/null
$MYL < sql/game/character_subclasses.sql &> /dev/null
$MYL < sql/game/character_summon_skills_save.sql &> /dev/null
$MYL < sql/game/character_summons.sql &> /dev/null
$MYL < sql/game/character_tpbookmark.sql &> /dev/null
$MYL < sql/game/character_ui_actions.sql &> /dev/null
$MYL < sql/game/character_ui_categories.sql &> /dev/null
$MYL < sql/game/character_variables.sql &> /dev/null
$MYL < sql/game/characters.sql &> /dev/null
$MYL < sql/game/clan_data.sql &> /dev/null
$MYL < sql/game/clan_notices.sql &> /dev/null
$MYL < sql/game/clan_privs.sql &> /dev/null
$MYL < sql/game/clan_skills.sql &> /dev/null
$MYL < sql/game/clan_subpledges.sql &> /dev/null
$MYL < sql/game/clan_wars.sql &> /dev/null
$MYL < sql/game/clanhall.sql &> /dev/null
$MYL < sql/game/clanhall_functions.sql &> /dev/null
$MYL < sql/game/clanhall_siege_attackers.sql &> /dev/null
$MYL < sql/game/clanhall_siege_guards.sql &> /dev/null
$MYL < sql/game/crests.sql &> /dev/null
$MYL < sql/game/cursed_weapons.sql &> /dev/null
$MYL < sql/game/dimensional_rift.sql &> /dev/null
$MYL < sql/game/fort.sql &> /dev/null
$MYL < sql/game/fort_doorupgrade.sql &> /dev/null
$MYL < sql/game/fort_functions.sql &> /dev/null
$MYL < sql/game/fort_siege_guards.sql &> /dev/null
$MYL < sql/game/fort_spawnlist.sql &> /dev/null
$MYL < sql/game/fortsiege_clans.sql &> /dev/null
$MYL < sql/game/forums.sql &> /dev/null
$MYL < sql/game/four_sepulchers_spawnlist.sql &> /dev/null
$MYL < sql/game/games.sql &> /dev/null
$MYL < sql/game/global_tasks.sql &> /dev/null
$MYL < sql/game/global_variables.sql &> /dev/null
$MYL < sql/game/grandboss_data.sql &> /dev/null
$MYL < sql/game/grandboss_list.sql &> /dev/null
$MYL < sql/game/herb_droplist_groups.sql &> /dev/null
$MYL < sql/game/heroes.sql &> /dev/null
$MYL < sql/game/heroes_diary.sql &> /dev/null
$MYL < sql/game/item_attributes.sql &> /dev/null
$MYL < sql/game/item_auction.sql &> /dev/null
$MYL < sql/game/item_auction_bid.sql &> /dev/null
$MYL < sql/game/item_elementals.sql &> /dev/null
$MYL < sql/game/items.sql &> /dev/null
$MYL < sql/game/itemsonground.sql &> /dev/null
$MYL < sql/game/locations.sql &> /dev/null
$MYL < sql/game/merchant_lease.sql &> /dev/null
$MYL < sql/game/messages.sql &> /dev/null
$MYL < sql/game/npc_buffer.sql &> /dev/null
$MYL < sql/game/olympiad_data.sql &> /dev/null
$MYL < sql/game/olympiad_fights.sql &> /dev/null
$MYL < sql/game/olympiad_nobles.sql &> /dev/null
$MYL < sql/game/olympiad_nobles_eom.sql &> /dev/null
$MYL < sql/game/petition_feedback.sql &> /dev/null
$MYL < sql/game/pets.sql &> /dev/null
$MYL < sql/game/pets_skills.sql &> /dev/null
$MYL < sql/game/posts.sql &> /dev/null
$MYL < sql/game/punishments.sql &> /dev/null
$MYL < sql/game/quest_global_data.sql &> /dev/null
$MYL < sql/game/raidboss_spawnlist.sql &> /dev/null
$MYL < sql/game/rainbowsprings_attacker_list.sql &> /dev/null
$MYL < sql/game/random_spawn.sql &> /dev/null
$MYL < sql/game/random_spawn_loc.sql &> /dev/null
$MYL < sql/game/seven_signs.sql &> /dev/null
$MYL < sql/game/seven_signs_festival.sql &> /dev/null
$MYL < sql/game/seven_signs_status.sql &> /dev/null
$MYL < sql/game/siegable_clanhall.sql &> /dev/null
$MYL < sql/game/siegable_hall_flagwar_attackers.sql &> /dev/null
$MYL < sql/game/siegable_hall_flagwar_attackers_members.sql &> /dev/null
$MYL < sql/game/siege_clans.sql &> /dev/null
$MYL < sql/game/spawnlist.sql &> /dev/null
$MYL < sql/game/teleport.sql &> /dev/null
$MYL < sql/game/territories.sql &> /dev/null
$MYL < sql/game/territory_registrations.sql &> /dev/null
$MYL < sql/game/territory_spawnlist.sql &> /dev/null
$MYL < sql/game/topic.sql &> /dev/null

$MYL < sql/custom/custom_npc_buffer.sql &> /dev/null
$MYL < sql/custom/custom_spawnlist.sql &> /dev/null
$MYL < sql/custom/custom_teleport.sql &> /dev/null
$MYL < sql/mods/mods_wedding.sql &> /dev/null

echo ""
echo "Was fast, isn't it ?"
}

finish(){
echo ""
echo "Script execution finished."
exit 0
}

clear
configure