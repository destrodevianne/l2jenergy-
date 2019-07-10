CREATE TABLE IF NOT EXISTS `pccafe_coupons` (
  `used_by` varchar(255) NOT NULL,
  `coupon_use`   INT UNSIGNED NOT NULL DEFAULT 0,
  `coupon_value` int(10) unsigned NOT NULL DEFAULT 0,
  `serial_code` text NOT NULL
  PRIMARY KEY (`coupon_use`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;