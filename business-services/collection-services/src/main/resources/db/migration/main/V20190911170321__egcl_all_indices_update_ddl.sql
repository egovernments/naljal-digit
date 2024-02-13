CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_bankaccountservicemapping ON egcl_bankaccountservicemapping USING btree (id);
CREATE INDEX IF NOT EXISTS  idx_ins_transactionnumber_v1 ON egcl_instrumentheader_v1 USING btree (transactionnumber);
CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_instrumenthead_v1 ON egcl_instrumentheader_v1 USING btree (id);
CREATE INDEX IF NOT EXISTS  idx_receiptdetails_v1_receiptheader ON egcl_receiptdetails_v1 USING btree (receiptheader);
CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_receiptdetails_v1 ON egcl_receiptdetails_v1 USING btree (id);
CREATE INDEX IF NOT EXISTS  idx_rcpthd_v1_business ON egcl_receiptheader_v1 USING btree (businessdetails);
CREATE INDEX IF NOT EXISTS  idx_rcpthd_v1_consumercode ON egcl_receiptheader_v1 USING btree (consumercode);
CREATE INDEX IF NOT EXISTS  idx_rcpthd_v1_mreceiptnumber ON egcl_receiptheader_v1 USING btree (manualreceiptnumber);
CREATE INDEX IF NOT EXISTS  idx_rcpthd_v1_refno ON egcl_receiptheader_v1 USING btree (referencenumber);
CREATE INDEX IF NOT EXISTS  idx_rcpthd_v1_status ON egcl_receiptheader_v1 USING btree (status);
CREATE INDEX IF NOT EXISTS  idx_rcpthd_v1_transactionid ON egcl_receiptheader_v1 USING btree (transactionid);
CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_receiptheader_v1 ON egcl_receiptheader_v1 USING btree (id);
CREATE INDEX IF NOT EXISTS  idx_receiptinstrument_v1_instrumentheader ON egcl_receiptinstrument_v1 USING btree (instrumentheader);
CREATE INDEX IF NOT EXISTS  idx_receiptinstrument_v1_receiptheader ON egcl_receiptinstrument_v1 USING btree (receiptheader);
CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_remittance ON egcl_remittance USING btree (id);
CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_remittancedetails ON egcl_remittancedetails USING btree (id);
CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_remittanceinstrument ON egcl_remittanceinstrument USING btree (id);
CREATE UNIQUE INDEX IF NOT EXISTS  pk_egcl_remittancereceipt ON egcl_remittancereceipt USING btree (id);
