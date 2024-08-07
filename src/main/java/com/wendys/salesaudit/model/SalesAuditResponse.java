package com.wendys.salesaudit.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesAuditResponse extends AuditEntry{

	private boolean allowDataEdits;
	private List<String> errorMsgList;
	private String actionMsg;


	public boolean isAllowDataEdits() {
		return allowDataEdits;
	}
	public void setAllowDataEdits(boolean allowDataEdits) {
		this.allowDataEdits = allowDataEdits;
	}

	public String getActionMsg() {
		return actionMsg;
	}
	public void setActionMsg(String actionMsg) {
		this.actionMsg = actionMsg;
	}
	public List<String> getErrorMsg() {
		return errorMsgList;
	}
	public void setErrorMsg(List<String> errorMsg) {
		this.errorMsgList = errorMsg;
	}


}