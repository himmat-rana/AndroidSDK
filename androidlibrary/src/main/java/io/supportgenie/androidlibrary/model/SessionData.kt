package io.supportgenie.androidlibrary.model

import androidx.room.*
import io.supportgenie.androidlibrary.data.network.dto.*
import java.util.*
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey



data class SessionData(val companyName: String, val companyLogo: String, val companyId:String, val sessionId: String, val status:String, val updatedAt: String) {
    constructor(companyDto: CompanyDto, sessionDto: SessionsDataDto<List<ParticipantDto>>) : this(companyDto.name, companyDto.photoUrl,
        companyDto.companyId, sessionDto.sessionId, sessionDto.status, sessionDto.updatedAt)  // Maps from DTO
}



/*


Database Structure

1. Company
- companyId					String
- localUpdatedAt		DateTime
- updatedAt 				String (server updatedAt string value)
- name							String
- photoUrl					String
- website						String
indexed by unique companyId

2. Agent
- userId						String
- companyId					String
- localUpdatedAt		DateTime
- updatedAt 				String (server updatedAt string value)
- name							String
- photoUrl					String
- email							String
- phone							String
- status						String
indexed by unique userId, also by companyId

3. User
- userId						String
- companyId					String
- localUpdatedAt		DateTime
- updatedAt 				String (server updatedAt string value)
- isActive						Boolean
indexed by unique userId, also by companyId

4. Session
- sessionId					String
- companyId					String
- createdAt 				DateTime
- localUpdatedAt		DateTime
- updatedAt 				String (server updatedAt string value)
- type							string (for now its always chat)
- status						String (init, active or closed)
- userId						String
indexed by unique sessionId, also by companyId, userId (foreign key userId in user table)

5. SessionParticipant
- sessionId					String
- companyId					String
- createdAt 				DateTime
- localUpdatedAt		DateTime
- updatedAt 				String (server updatedAt string value)
- status						String (init, active or closed)
- participant				String (either userId from Agent table or userId from User table)
- participantType		String (customer, agent or bot)
indexed by unqiue of (sessionId, participant, participantType) also by companyId
I think we need to add a Bot table as well, i will double check

6. ChatMessage
- sessionId					String
- companyId					String
- createdAt 				DateTime
- localUpdatedAt		DateTime
- updatedAt 				String (server updatedAt string value)
- deliveryStatus		String (unsent, sent)
- sender 		 				String (either userId from Agent table or userId from User table)
- senderType				String (customer, agent or bot)
- messageId					String
- messageTime				DateTime
- mimeType					String
- message 					String
- extraMessageData	String
- retries						Int   (incase we need to redeliver the message because of failure)
- localFile					String (please check this for android, in case sending fails we might need this)
- progress					Float (please check this for android, upload progress , we may not need this)
indexed by unqiue messageId, also by companyId, sessionId, sender






 */


