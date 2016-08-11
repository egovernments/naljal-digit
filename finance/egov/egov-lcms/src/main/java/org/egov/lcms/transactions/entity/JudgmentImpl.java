/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.lcms.transactions.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.ValidateDate;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.lcms.masters.entity.enums.ImplementationFailure;
import org.egov.lcms.masters.entity.enums.JudgmentImplIsComplied;
import org.egov.lcms.utils.constants.LcmsConstants;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGLC_JUDGMENTIMPL")
@SequenceGenerator(name = JudgmentImpl.SEQ_EGLC_JUDGMENTIMPL, sequenceName = JudgmentImpl.SEQ_EGLC_JUDGMENTIMPL, allocationSize = 1)
public class JudgmentImpl extends AbstractAuditable {

    private static final long serialVersionUID = 1517694643078084884L;
    public static final String SEQ_EGLC_JUDGMENTIMPL = "SEQ_EGLC_JUDGMENTIMPL";

    @Id
    @GeneratedValue(generator = SEQ_EGLC_JUDGMENTIMPL, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "judgment", nullable = false)
    private Judgment judgment;

   
    @Enumerated(EnumType.STRING)
    @Column(name = "iscompiled")
    private JudgmentImplIsComplied judgmentImplIsComplied;

  
    @Temporal(TemporalType.DATE)
    @Column(name = "dateofcompliance")
    @ValidateDate(allowPast = true, dateFormat = LcmsConstants.DATE_FORMAT, message = "invalid.compliance.date")
    private Date dateOfCompliance;

    @Length(max = 1024)
    @Column(name = "compliancereport")
    private String complianceReport;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private ImplementationFailure implementationFailure;

    @Length(max = 128)
    @Column(name = "implementationdetails")
    private String details;

    @OneToMany(mappedBy = "judgmentImpl", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contempt> contempt = new ArrayList<Contempt>(0);

    @OneToMany(mappedBy = "judgmentImpl", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appeal> appeal = new ArrayList<Appeal>(0);

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public List<Contempt> getContempt() {
        return contempt;
    }

    public void setContempt(final List<Contempt> contempt) {
        this.contempt = contempt;
    }

    public List<Appeal> getAppeal() {
        return appeal;
    }

    public void setAppeal(final List<Appeal> appeal) {
        this.appeal = appeal;
    }

    public Judgment getJudgment() {
        return judgment;
    }

    public void setJudgment(final Judgment judgment) {
        this.judgment = judgment;
    }

    public JudgmentImplIsComplied getJudgmentImplIsComplied() {
        return judgmentImplIsComplied;
    }

    public void setJudgmentImplIsComplied(final JudgmentImplIsComplied judgmentImplIsComplied) {
        this.judgmentImplIsComplied = judgmentImplIsComplied;
    }

    public ImplementationFailure getImplementationFailure() {
        return implementationFailure;
    }

    public void setImplementationFailure(final ImplementationFailure implementationFailure) {
        this.implementationFailure = implementationFailure;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(final String details) {
        this.details = details;
    }

    public Date getDateOfCompliance() {
        return dateOfCompliance;
    }

    public void setDateOfCompliance(final Date dateOfCompliance) {
        this.dateOfCompliance = dateOfCompliance;
    }

    public String getComplianceReport() {
        return complianceReport;
    }

    public void setComplianceReport(final String complianceReport) {
        this.complianceReport = complianceReport;
    }

    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (getDateOfCompliance() != null && !DateUtils.compareDates(getDateOfCompliance(), judgment.getOrderDate()))
            errors.add(new ValidationError("dateofcompliance", "dateofcompliance.less.orderDate"));
        for (final Contempt contempt : getContempt())
            errors.addAll(contempt.validate());
        for (final Appeal appeal : getAppeal())
            errors.addAll(appeal.validate());
        return errors;
    }

}