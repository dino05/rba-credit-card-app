import React, { useState } from 'react';
import { clientAPI } from '../services/api';

const ClientForm = ({ onClientCreated }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    oib: '',
    cardStatus: 'PENDING'
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      if (formData.oib.length !== 11) {
        setMessage('OIB must be exactly 11 characters');
        return;
      }

      const response = await clientAPI.createClient(formData);
      setMessage('Client created successfully!');
      setFormData({
        firstName: '',
        lastName: '',
        oib: '',
        cardStatus: 'PENDING'
      });
      
      if (onClientCreated) {
        onClientCreated(response.data);
      }
    } catch (error) {
      setMessage(error.response?.data?.message || 'Error creating client');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>Create New Client</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="firstName">First Name:</label>
          <input
            type="text"
            id="firstName"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="lastName">Last Name:</label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="oib">OIB:</label>
          <input
            type="text"
            id="oib"
            name="oib"
            value={formData.oib}
            onChange={handleChange}
            maxLength="11"
            required
          />
          <small>Must be exactly 11 characters</small>
        </div>

        <div className="form-group">
          <label htmlFor="cardStatus">Card Status:</label>
          <select
            id="cardStatus"
            name="cardStatus"
            value={formData.cardStatus}
            onChange={handleChange}
          >
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Creating...' : 'Create Client'}
        </button>

        {message && (
          <div className={`message ${message.includes('Error') ? 'error' : 'success'}`}>
            {message}
          </div>
        )}
      </form>
    </div>
  );
};

export default ClientForm;