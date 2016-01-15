
cnf(activR12a, axiom, [r_R12a, -m_mmcoa_R]).
cnf(prodR12a, axiom, [m_mmcoa_S, -r_R12a]).
cnf(prodR49, axiom, [c_adp_glyc3p_h, -r_R49]).
cnf(decadp_glyc3p_h2, axiom, [m_glyc3p, -c_adp_glyc3p_h]).
cnf(activR51a, axiom, [r_R51a, -c_glyc3p_nadp]).
cnf(prodR51a, axiom, [c_dhap_h_nadph, -r_R51a]).
cnf(activR105, axiom, [r_R105, -m_succoa]).
cnf(prodR105, axiom, [m_mmcoa_R, -r_R105]).
cnf(decppa_succoa2, axiom, [m_succoa, -c_ppa_succoa]).
cnf(activR145, axiom, [r_R145, -m_5mdru1p]).
cnf(prodR145, axiom, [c_dkmpp_h2o, -r_R145]).
cnf(decdkmpp_h2o1, axiom, [m_dkmpp, -c_dkmpp_h2o]).
cnf(activR146, axiom, [r_R146, -c_dkmpp_h2o]).
cnf(combidkmpp_h2o, axiom, [c_dkmpp_h2o, -m_dkmpp, -m_h2o]).
cnf(activR150, axiom, [r_R150, -c_5mtr_atp]).
cnf(combi5mtr_atp, axiom, [c_5mtr_atp, -m_5mtr, -m_atp]).
cnf(prodR150, axiom, [c_5mdr1p_adp_h, -r_R150]).
cnf(dec5mdr1p_adp_h1, axiom, [m_5mdr1p, -c_5mdr1p_adp_h]).
cnf(activR151a, axiom, [r_R151a, -m_5mdr1p]).
cnf(prodR151a, axiom, [m_5mdru1p, -r_R151a]).
cnf(activR161, axiom, [r_R161, -c_asp_L_atp_citr_L]).
cnf(prodR161, axiom, [c_amp_argsuc_h_ppi, -r_R161]).
cnf(decamp_argsuc_h_ppi2, axiom, [m_argsuc, -c_amp_argsuc_h_ppi]).
cnf(activR162a, axiom, [r_R162a, -m_argsuc]).
cnf(prodR162a, axiom, [c_arg_L_fum, -r_R162a]).
cnf(activR164, axiom, [r_R164, -c_arg_L_succoa]).
cnf(combiarg_L_succoa, axiom, [c_arg_L_succoa, -m_arg_L, -m_succoa]).
cnf(prodR164, axiom, [c_coa_h_sucarg, -r_R164]).
cnf(deccoa_h_sucarg3, axiom, [m_sucarg, -c_coa_h_sucarg]).
cnf(activR174, axiom, [r_R174, -c_5mta_h2o]).
cnf(combi5mta_h2o, axiom, [c_5mta_h2o, -m_5mta, -m_h2o]).
cnf(prodR174, axiom, [c_5mtr_ade, -r_R174]).
cnf(dec5mtr_ade1, axiom, [m_5mtr, -c_5mtr_ade]).
cnf(activR180, axiom, [r_R180, -c_arg_L_h]).
cnf(prodR180, axiom, [c_agm_co2, -r_R180]).
cnf(decagm_co21, axiom, [m_agm, -c_agm_co2]).
cnf(activR181, axiom, [r_R181, -c_agm_h2o]).
cnf(combiagm_h2o, axiom, [c_agm_h2o, -m_agm, -m_h2o]).
cnf(prodR181, axiom, [c_ptrc_urea, -r_R181]).
cnf(decptrc_urea1, axiom, [m_ptrc, -c_ptrc_urea]).
cnf(decptrc_urea2, axiom, [m_urea, -c_ptrc_urea]).
cnf(activR182, axiom, [r_R182, -c_h_orn]).
cnf(prodR182, axiom, [c_co2_ptrc, -r_R182]).
cnf(decco2_ptrc2, axiom, [m_ptrc, -c_co2_ptrc]).
cnf(activR184, axiom, [r_R184, -c_ametam_ptrc]).
cnf(combiametam_ptrc, axiom, [c_ametam_ptrc, -m_ametam, -m_ptrc]).
cnf(prodR184, axiom, [c_5mta_h_spmd, -r_R184]).
cnf(dec5mta_h_spmd1, axiom, [m_5mta, -c_5mta_h_spmd]).
cnf(activR192, axiom, [r_R192, -c_h2o_pa_EC]).
cnf(combih2o_pa_EC, axiom, [c_h2o_pa_EC, -m_h2o, -m_pa_EC]).
cnf(activR194, axiom, [r_R194, -c_ACP_atp_ttdca]).
cnf(prodR194, axiom, [c_amp_myrsACP_ppi, -r_R194]).
cnf(decamp_myrsACP_ppi2, axiom, [m_myrsACP, -c_amp_myrsACP_ppi]).
cnf(activR195, axiom, [r_R195, -c_ACP_atp_ttdcea]).
cnf(prodR195, axiom, [c_amp_ppi_tdeACP, -r_R195]).
cnf(decamp_ppi_tdeACP3, axiom, [m_tdeACP, -c_amp_ppi_tdeACP]).
cnf(decamp_palmACP_ppi2, axiom, [m_palmACP, -c_amp_palmACP_ppi]).
cnf(prodR197, axiom, [c_amp_hdeACP_ppi, -r_R197]).
cnf(decamp_hdeACP_ppi2, axiom, [m_hdeACP, -c_amp_hdeACP_ppi]).
cnf(activR198, axiom, [r_R198, -c_ACP_atp_ocdcea]).
cnf(prodR198, axiom, [c_amp_octeACP_ppi, -r_R198]).
cnf(decamp_octeACP_ppi2, axiom, [m_octeACP, -c_amp_octeACP_ppi]).
cnf(prodR200, axiom, [c_adp_h_pa_EC, -r_R200]).
cnf(decadp_h_pa_EC3, axiom, [m_pa_EC, -c_adp_h_pa_EC]).
cnf(activR201, axiom, [r_R201, -m_etha]).
cnf(activR208, axiom, [r_R208, -c_g3pc_h2o]).
cnf(combig3pc_h2o, axiom, [c_g3pc_h2o, -m_g3pc, -m_h2o]).
cnf(prodR208, axiom, [c_chol_glyc3p_h, -r_R208]).
cnf(decchol_glyc3p_h1, axiom, [m_chol, -c_chol_glyc3p_h]).
cnf(decchol_glyc3p_h2, axiom, [m_glyc3p, -c_chol_glyc3p_h]).
cnf(activR209, axiom, [r_R209, -c_g3pe_h2o]).
cnf(combig3pe_h2o, axiom, [c_g3pe_h2o, -m_g3pe, -m_h2o]).
cnf(prodR209, axiom, [c_etha_glyc3p_h, -r_R209]).
cnf(decetha_glyc3p_h1, axiom, [m_etha, -c_etha_glyc3p_h]).
cnf(decetha_glyc3p_h2, axiom, [m_glyc3p, -c_etha_glyc3p_h]).
cnf(decglyc3p_h_ser_L1, axiom, [m_glyc3p, -c_glyc3p_h_ser_L]).
cnf(decglyc_glyc3p_h2, axiom, [m_glyc3p, -c_glyc_glyc3p_h]).
cnf(activR212, axiom, [r_R212, -c_g3pi_h2o]).
cnf(combig3pi_h2o, axiom, [c_g3pi_h2o, -m_g3pi, -m_h2o]).
cnf(prodR212, axiom, [c_glyc3p_h_inost, -r_R212]).
cnf(decglyc3p_h_inost1, axiom, [m_glyc3p, -c_glyc3p_h_inost]).
cnf(decglyc3p_h_inost3, axiom, [m_inost, -c_glyc3p_h_inost]).
cnf(prodR217, axiom, [c_ckdo_ppi, -r_R217]).
cnf(decckdo_ppi1, axiom, [m_ckdo, -c_ckdo_ppi]).
cnf(activR218, axiom, [r_R218, -c_ckdo_lipidA]).
cnf(combickdo_lipidA, axiom, [c_ckdo_lipidA, -m_ckdo, -m_lipidA]).
cnf(prodR218, axiom, [c_cmp_h_kdolipid4, -r_R218]).
cnf(deccmp_h_kdolipid41, axiom, [m_cmp, -c_cmp_h_kdolipid4]).
cnf(deccmp_h_kdolipid43, axiom, [m_kdolipid4, -c_cmp_h_kdolipid4]).
cnf(activR219, axiom, [r_R219, -c_ckdo_kdolipid4]).
cnf(combickdo_kdolipid4, axiom, [c_ckdo_kdolipid4, -m_ckdo, -m_kdolipid4]).
cnf(prodR219, axiom, [c_cmp_h_kdo2lipid4, -r_R219]).
cnf(deccmp_h_kdo2lipid41, axiom, [m_cmp, -c_cmp_h_kdo2lipid4]).
cnf(deccmp_h_kdo2lipid43, axiom, [m_kdo2lipid4, -c_cmp_h_kdo2lipid4]).
cnf(activR224, axiom, [r_R224, -c_atp_lipidAds]).
cnf(prodR224, axiom, [c_adp_h_lipidA, -r_R224]).
cnf(decadp_h_lipidA3, axiom, [m_lipidA, -c_adp_h_lipidA]).
cnf(activR225, axiom, [r_R225, -c_ddcaACP_kdo2lipid4]).
cnf(combiddcaACP_kdo2lipid4, axiom, [c_ddcaACP_kdo2lipid4, -m_ddcaACP, -m_kdo2lipid4]).
cnf(prodR225, axiom, [c_ACP_kdo2lipid4L, -r_R225]).
cnf(decACP_kdo2lipid4L2, axiom, [m_kdo2lipid4L, -c_ACP_kdo2lipid4L]).
cnf(activR226, axiom, [r_R226, -c_hdeACP_kdo2lipid4]).
cnf(combihdeACP_kdo2lipid4, axiom, [c_hdeACP_kdo2lipid4, -m_hdeACP, -m_kdo2lipid4]).
cnf(prodR226, axiom, [c_ACP_kdo2lipid4p, -r_R226]).
cnf(decACP_kdo2lipid4p2, axiom, [m_kdo2lipid4p, -c_ACP_kdo2lipid4p]).
cnf(activR230, axiom, [r_R230, -c_kdo2lipid4p_myrsACP]).
cnf(combikdo2lipid4p_myrsACP, axiom, [c_kdo2lipid4p_myrsACP, -m_kdo2lipid4p, -m_myrsACP]).
cnf(prodR230, axiom, [c_ACP_lipa_cold, -r_R230]).
cnf(decACP_lipa_cold2, axiom, [m_lipa_cold, -c_ACP_lipa_cold]).
cnf(activR231, axiom, [r_R231, -c_kdo2lipid4L_myrsACP]).
cnf(combikdo2lipid4L_myrsACP, axiom, [c_kdo2lipid4L_myrsACP, -m_kdo2lipid4L, -m_myrsACP]).
cnf(prodR231, axiom, [c_ACP_lipa, -r_R231]).
cnf(decACP_lipa2, axiom, [m_lipa, -c_ACP_lipa]).
cnf(decg3pe_h_hdca_hdcea_ocdcea_ttdca_ttdcea1, axiom, [m_g3pe, -c_g3pe_h_hdca_hdcea_ocdcea_ttdca_ttdcea]).
cnf(activR245, axiom, [r_R245, -c_agpc_EC_h2o]).
cnf(combiagpc_EC_h2o, axiom, [c_agpc_EC_h2o, -m_agpc_EC, -m_h2o]).
cnf(prodR245, axiom, [c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea, -r_R245]).
cnf(decg3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea1, axiom, [m_g3pc, -c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea]).
cnf(decg3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea3, axiom, [m_hdca, -c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea]).
cnf(decg3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea4, axiom, [m_hdcea, -c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea]).
cnf(decg3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea5, axiom, [m_ocdcea, -c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea]).
cnf(decg3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea6, axiom, [m_ttdca, -c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea]).
cnf(activR246, axiom, [r_R246, -c_agpe_EC_pg_EC]).
cnf(prodR246, axiom, [c_apg_EC_g3pe, -r_R246]).
cnf(decapg_EC_g3pe1, axiom, [m_apg_EC, -c_apg_EC_g3pe]).
cnf(decapg_EC_g3pe2, axiom, [m_g3pe, -c_apg_EC_g3pe]).
cnf(activR247, axiom, [r_R247, -c_agpc_EC_pg_EC]).
cnf(prodR247, axiom, [c_apg_EC_g3pc, -r_R247]).
cnf(decapg_EC_g3pc1, axiom, [m_apg_EC, -c_apg_EC_g3pc]).
cnf(decapg_EC_g3pc2, axiom, [m_g3pc, -c_apg_EC_g3pc]).
cnf(activR270, axiom, [r_R270, -c_2dmmql8_fum]).
cnf(prodR270, axiom, [c_2dmmq8_succ, -r_R270]).
cnf(dec2dmmq8_succ1, axiom, [m_2dmmq8, -c_2dmmq8_succ]).
cnf(activR274, axiom, [r_R274, -c_akg_coa_nad]).
cnf(prodR274, axiom, [c_co2_nadh_succoa, -r_R274]).
cnf(decco2_nadh_succoa3, axiom, [m_succoa, -c_co2_nadh_succoa]).
cnf(prodR279a, axiom, [c_adp_pi_succoa, -r_R279a]).
cnf(decadp_pi_succoa3, axiom, [m_succoa, -c_adp_pi_succoa]).
cnf(prodR292, axiom, [c_4ppcys_cmp_h_ppi, -r_R292]).
cnf(dec4ppcys_cmp_h_ppi1, axiom, [m_4ppcys, -c_4ppcys_cmp_h_ppi]).
cnf(dec4ppcys_cmp_h_ppi2, axiom, [m_cmp, -c_4ppcys_cmp_h_ppi]).
cnf(activR346, axiom, [r_R346, -c_2me4p_ctp_h]).
cnf(prodR346, axiom, [c_4c2me_ppi, -r_R346]).
cnf(dec4c2me_ppi1, axiom, [m_4c2me, -c_4c2me_ppi]).
cnf(activR347, axiom, [r_R347, -c_4c2me_atp]).
cnf(combi4c2me_atp, axiom, [c_4c2me_atp, -m_4c2me, -m_atp]).
cnf(prodR347, axiom, [c_2p4c2me_adp_h, -r_R347]).
cnf(dec2p4c2me_adp_h1, axiom, [m_2p4c2me, -c_2p4c2me_adp_h]).
cnf(activR348, axiom, [r_R348, -m_2p4c2me]).
cnf(prodR348, axiom, [c_2mecdp_cmp, -r_R348]).
cnf(dec2mecdp_cmp1, axiom, [m_2mecdp, -c_2mecdp_cmp]).
cnf(dec2mecdp_cmp2, axiom, [m_cmp, -c_2mecdp_cmp]).
cnf(activR476, axiom, [r_R476, -c_cdpdag1_h2o]).
cnf(prodR476, axiom, [c_cmp_h_pa_EC, -r_R476]).
cnf(deccmp_h_pa_EC1, axiom, [m_cmp, -c_cmp_h_pa_EC]).
cnf(deccmp_h_pa_EC3, axiom, [m_pa_EC, -c_cmp_h_pa_EC]).
cnf(decACP_co2_h2o_myrsACP_nadp4, axiom, [m_myrsACP, -c_ACP_co2_h2o_myrsACP_nadp]).
cnf(decACP_co2_ddcaACP_h2o_nadp3, axiom, [m_ddcaACP, -c_ACP_co2_ddcaACP_h2o_nadp]).
cnf(decACP_co2_h2o_nadp_palmACP5, axiom, [m_palmACP, -c_ACP_co2_h2o_nadp_palmACP]).
cnf(decACP_co2_h2o_nadp_octeACP5, axiom, [m_octeACP, -c_ACP_co2_h2o_nadp_octeACP]).
cnf(decACP_co2_h2o_nadp_tdeACP5, axiom, [m_tdeACP, -c_ACP_co2_h2o_nadp_tdeACP]).
cnf(activR490, axiom, [r_R490, -c_actACP_h_malACP_nadph]).
cnf(prodR490, axiom, [c_ACP_co2_h2o_hdeACP_nadp, -r_R490]).
cnf(decACP_co2_h2o_hdeACP_nadp4, axiom, [m_hdeACP, -c_ACP_co2_h2o_hdeACP_nadp]).
cnf(activR494, axiom, [r_R494, -c_h2o_pgp_EC]).
cnf(combih2o_pgp_EC, axiom, [c_h2o_pgp_EC, -m_h2o, -m_pgp_EC]).
cnf(activR495a, axiom, [r_R495a, -c_cdpdag1_glyc3p]).
cnf(combicdpdag1_glyc3p, axiom, [c_cdpdag1_glyc3p, -m_cdpdag1, -m_glyc3p]).
cnf(prodR495a, axiom, [c_cmp_h_pgp_EC, -r_R495a]).
cnf(deccmp_h_pgp_EC1, axiom, [m_cmp, -c_cmp_h_pgp_EC]).
cnf(deccmp_h_pgp_EC3, axiom, [m_pgp_EC, -c_cmp_h_pgp_EC]).
cnf(activR496, axiom, [r_R496, -c_glyc3p_hdeACP_myrsACP_octeACP_palmACP_tdeACP]).
cnf(combiglyc3p_hdeACP_myrsACP_octeACP_palmACP_tdeACP, axiom, [c_glyc3p_hdeACP_myrsACP_octeACP_palmACP_tdeACP, -m_glyc3p, -m_hdeACP, -m_myrsACP, -m_octeACP, -m_palmACP, -m_tdeACP]).
cnf(prodR496, axiom, [c_ACP_pa_EC, -r_R496]).
cnf(decACP_pa_EC2, axiom, [m_pa_EC, -c_ACP_pa_EC]).
cnf(combihom_L_succoa, axiom, [c_hom_L_succoa, -m_hom_L, -m_succoa]).
cnf(activR515, axiom, [r_R515, -c_cmp_h2o]).
cnf(combicmp_h2o, axiom, [c_cmp_h2o, -m_cmp, -m_h2o]).
cnf(prodR515, axiom, [c_csn_r5p, -r_R515]).
cnf(deccsn_r5p1, axiom, [m_csn, -c_csn_r5p]).
cnf(activR531a, axiom, [r_R531a, -c_atp_cmp]).
cnf(combiatp_cmp, axiom, [c_atp_cmp, -m_atp, -m_cmp]).
cnf(prodR531a, axiom, [c_adp_cdp, -r_R531a]).
cnf(activR549, axiom, [r_R549, -c_ctp_h2o]).
cnf(prodR549, axiom, [c_cmp_h_ppi, -r_R549]).
cnf(deccmp_h_ppi1, axiom, [m_cmp, -c_cmp_h_ppi]).
cnf(activR578, axiom, [r_R578, -c_cytd_gtp]).
cnf(prodR578, axiom, [c_cmp_gdp_h, -r_R578]).
cnf(deccmp_gdp_h1, axiom, [m_cmp, -c_cmp_gdp_h]).
cnf(activR586, axiom, [r_R586, -c_cmp_h2o]).
cnf(prodR586, axiom, [c_cytd_pi, -r_R586]).
cnf(deccytd_pi1, axiom, [m_cytd, -c_cytd_pi]).
cnf(activR617, axiom, [r_R617, -c_2dmmq8_glyc3p]).
cnf(combi2dmmq8_glyc3p, axiom, [c_2dmmq8_glyc3p, -m_2dmmq8, -m_glyc3p]).
cnf(activR618, axiom, [r_R618, -c_glyc3p_mqn8]).
cnf(combiglyc3p_mqn8, axiom, [c_glyc3p_mqn8, -m_glyc3p, -m_mqn8]).
cnf(activR619, axiom, [r_R619, -c_glyc3p_q8]).
cnf(combiglyc3p_q8, axiom, [c_glyc3p_q8, -m_glyc3p, -m_q8]).
cnf(prodR619, axiom, [c_dhap_q8h2, -r_R619]).
cnf(decdhap_q8h22, axiom, [m_q8h2, -c_dhap_q8h2]).
cnf(activR684, axiom, [r_R684, -c_23dhdp_h_nadph]).
cnf(prodR684, axiom, [c_nadp_thdp, -r_R684]).
cnf(decnadp_thdp2, axiom, [m_thdp, -c_nadp_thdp]).
cnf(activR685, axiom, [r_R685, -c_h2o_succoa_thdp]).
cnf(combih2o_succoa_thdp, axiom, [c_h2o_succoa_thdp, -m_h2o, -m_succoa, -m_thdp]).
cnf(prodR685, axiom, [c_coa_sl2a6o, -r_R685]).
cnf(deccoa_sl2a6o2, axiom, [m_sl2a6o, -c_coa_sl2a6o]).
cnf(activR12b, axiom, [r_R12b, -m_mmcoa_S]).
cnf(prodR12b, axiom, [m_mmcoa_R, -r_R12b]).
cnf(activR51b, axiom, [r_R51b, -c_dhap_h_nadph]).
cnf(prodR51b, axiom, [c_glyc3p_nadp, -r_R51b]).
cnf(decglyc3p_nadp1, axiom, [m_glyc3p, -c_glyc3p_nadp]).
cnf(activR151b, axiom, [r_R151b, -m_5mdru1p]).
cnf(prodR151b, axiom, [m_5mdr1p, -r_R151b]).
cnf(activR162b, axiom, [r_R162b, -c_arg_L_fum]).
cnf(prodR162b, axiom, [m_argsuc, -r_R162b]).
cnf(deccmp_h_pe_EC1, axiom, [m_cmp, -c_cmp_h_pe_EC]).
cnf(activR279b, axiom, [r_R279b, -c_adp_pi_succoa]).
cnf(activR495b, axiom, [r_R495b, -c_cmp_h_pgp_EC]).
cnf(prodR495b, axiom, [c_cdpdag1_glyc3p, -r_R495b]).
cnf(deccdpdag1_glyc3p1, axiom, [m_cdpdag1, -c_cdpdag1_glyc3p]).
cnf(deccdpdag1_glyc3p2, axiom, [m_glyc3p, -c_cdpdag1_glyc3p]).
cnf(activR531b, axiom, [r_R531b, -c_adp_cdp]).
cnf(prodR531b, axiom, [c_atp_cmp, -r_R531b]).
cnf(decatp_cmp2, axiom, [m_cmp, -c_atp_cmp]).

pf([m_glyc3p, -m_glyc3p, m_cmp, -m_cmp, m_h2o, -m_h2o, m_succoa, -m_succoa, c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea, -c_g3pc_h_hdca_hdcea_ocdcea_ttdca_ttdcea, m_myrsACP, -m_myrsACP, c_cdpdag1_glyc3p, -c_cdpdag1_glyc3p, c_dkmpp_h2o, -c_dkmpp_h2o, m_pa_EC, -m_pa_EC, m_hdeACP, -m_hdeACP, c_cmp_h_pgp_EC, -c_cmp_h_pgp_EC]).
