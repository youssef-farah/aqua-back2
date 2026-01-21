package com.example.aqua.payement;

public class VerifyPaymentResponse {

	
	
	 private String status; // SUCCESS, FAILED, PENDING
	    private boolean confirmed;
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public boolean isConfirmed() {
			return confirmed;
		}
		public void setConfirmed(boolean confirmed) {
			this.confirmed = confirmed;
		}
	    
	    
	    
}
