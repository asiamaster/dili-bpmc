package com.dili.bpmc.sdk.domain;

import com.dili.ss.dto.IDTO;
import com.dili.ss.java.B;


/**
 * 流程定义映射
 * An object structure representing an executable process composed of
 * activities and transitions.
 *
 * Business processes are often created with graphical editors that store the
 * process definition in certain file format. These files can be added to a
 * Deployment artifact, such as for example a Business Archive (.bar)
 * file.
 *
 * At deploy time, the engine will then parse the process definition files to an
 * executable instance of this class, that can be used to start a ProcessInstance.
 * @author: WM
 * @time: 2020/10/21 10:31
 */
public interface ProcessDefinitionMapping extends IDTO {

    /** unique identifier */
    String getId();
    void setId(String id);

    /** category name which is derived from the targetNamespace attribute in the definitions element */
    String getCategory();
    void setCategory(String category);
    /** label used for display purposes */
    String getName();
    void setName(String name);
    /** unique name for all versions this process definitions */
    String getKey();
    void setKey(String key);

    /** description of this process **/
    String getDescription();
    void setDescription(String description);

    /** version of this process definition */
    Integer getVersion();
    void setVersion(Integer version);

    /** name of RepositoryService#getResourceAsStream(String, String) the resource
     * of this process definition. */
    String getResourceName();
    void setResourceName(String resourceName);

    /** The deployment in which this process definition is contained. */
    String getDeploymentId();
    void setDeploymentId(String deploymentId);

    /** The resource name in the deployment of the diagram image (if any). */
    String getDiagramResourceName();
    void setDiagramResourceName(String diagramResourceName);

    /** Does this process definition has a {@link FormService#getStartFormData(String) start form key}. */
    Boolean getHasStartFormKey();
    void setHasStartFormKey(Boolean hasStartFormKey);

    /** Does this process definition has a graphical notation defined (such that a diagram can be generated)? */
    Boolean getHasGraphicalNotation();
    void setHasGraphicalNotation(Boolean hasGraphicalNotation);

    /** Returns true if the process definition is in suspended state. */
    Boolean getIsSuspended();
    void setIsSuspended(Boolean isSuspended);

    /** The tenant identifier of this process definition */
    String getTenantId();
    void setTenantId(String tenantId);

}
