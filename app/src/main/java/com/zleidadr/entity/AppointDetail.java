package com.zleidadr.entity;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xiaoxuli on 16/1/7.
 */
public class AppointDetail extends SugarRecord {

    @Unique
    private String projectId;
    private String projectAppointId;
    private String loanee_id_card;
    private String loanee_name;
    private String loanee_sex;
    private String loanee_home;
    private String loanee_mobile;
    private String loanee_company;
    private String loanee_home_address;
    private String loan_date;
    private String authorized_agency;
    private String authorize_amount;
    private String repayment_bank;
    private String loanee_company_address;
    private String loanee_business_city;
    private String receipt_name;
    private String overdue_date;
    private String project_code;
    private String repayment_bank_id;
    private String can_urge_date;
    private String remarks1;
    private String remarks2;
    private String remarks3;
    private String project_files;

    public AppointDetail() {
    }

    public String getLoanee_id_card() {
        return loanee_id_card;
    }

    public void setLoanee_id_card(String loanee_id_card) {
        this.loanee_id_card = loanee_id_card;
    }

    public String getLoanee_name() {
        return loanee_name;
    }

    public void setLoanee_name(String loanee_name) {
        this.loanee_name = loanee_name;
    }

    public String getLoanee_sex() {
        return loanee_sex;
    }

    public void setLoanee_sex(String loanee_sex) {
        this.loanee_sex = loanee_sex;
    }

    public String getLoanee_home() {
        return loanee_home;
    }

    public void setLoanee_home(String loanee_home) {
        this.loanee_home = loanee_home;
    }

    public String getLoanee_mobile() {
        return loanee_mobile;
    }

    public void setLoanee_mobile(String loanee_mobile) {
        this.loanee_mobile = loanee_mobile;
    }

    public String getLoanee_company() {
        return loanee_company;
    }

    public void setLoanee_company(String loanee_company) {
        this.loanee_company = loanee_company;
    }

    public String getLoanee_home_address() {
        return loanee_home_address;
    }

    public void setLoanee_home_address(String loanee_home_address) {
        this.loanee_home_address = loanee_home_address;
    }

    public String getLoan_date() {
        return loan_date;
    }

    public void setLoan_date(String loan_date) {
        this.loan_date = loan_date;
    }

    public String getAuthorized_agency() {
        return authorized_agency;
    }

    public void setAuthorized_agency(String authorized_agency) {
        this.authorized_agency = authorized_agency;
    }

    public String getAuthorize_amount() {
        return authorize_amount;
    }

    public void setAuthorize_amount(String authorize_amount) {
        this.authorize_amount = authorize_amount;
    }

    public String getRepayment_bank() {
        return repayment_bank;
    }

    public void setRepayment_bank(String repayment_bank) {
        this.repayment_bank = repayment_bank;
    }

    public String getLoanee_company_address() {
        return loanee_company_address;
    }

    public void setLoanee_company_address(String loanee_company_address) {
        this.loanee_company_address = loanee_company_address;
    }

    public String getLoanee_business_city() {
        return loanee_business_city;
    }

    public void setLoanee_business_city(String loanee_business_city) {
        this.loanee_business_city = loanee_business_city;
    }

    public String getReceipt_name() {
        return receipt_name;
    }

    public void setReceipt_name(String receipt_name) {
        this.receipt_name = receipt_name;
    }

    public String getOverdue_date() {
        return overdue_date;
    }

    public void setOverdue_date(String overdue_date) {
        this.overdue_date = overdue_date;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public String getRepayment_bank_id() {
        return repayment_bank_id;
    }

    public void setRepayment_bank_id(String repayment_bank_id) {
        this.repayment_bank_id = repayment_bank_id;
    }

    public String getCan_urge_date() {
        return can_urge_date;
    }

    public void setCan_urge_date(String can_urge_date) {
        this.can_urge_date = can_urge_date;
    }

    public String getRemarks1() {
        return remarks1;
    }

    public void setRemarks1(String remarks1) {
        this.remarks1 = remarks1;
    }

    public String getRemarks2() {
        return remarks2;
    }

    public void setRemarks2(String remarks2) {
        this.remarks2 = remarks2;
    }

    public String getRemarks3() {
        return remarks3;
    }

    public void setRemarks3(String remarks3) {
        this.remarks3 = remarks3;
    }

    public String getProject_files() {
        return project_files;
    }

    public void setProject_files(String project_files) {
        this.project_files = project_files;
    }

    public String getProjectAppointId() {
        return projectAppointId;
    }

    public void setProjectAppointId(String projectAppointId) {
        this.projectAppointId = projectAppointId;
    }
}
